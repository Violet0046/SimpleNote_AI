package com.simplenote.backend.interaction.stream;

import com.simplenote.backend.interaction.InteractionRedisKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.PendingMessage;
import org.springframework.data.redis.connection.stream.PendingMessages;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Component
public class InteractionStreamReclaimer {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private InteractionStreamProperties interactionStreamProperties;

    @Autowired
    private InteractionStreamDeadLetterService deadLetterService;

    @SuppressWarnings("null")
    @Scheduled(fixedDelayString = "${social.stream.reclaim-fixed-delay-ms}")
    public void reclaimTimedOutMessages() {
        PendingMessages pendingMessages = stringRedisTemplate.opsForStream().pending(
                InteractionRedisKeys.SOCIAL_INTERACTION_STREAM_KEY,
                interactionStreamProperties.getGroup(),
                Range.unbounded(),
                interactionStreamProperties.getReclaimBatchSize()
        );

        if (pendingMessages == null || pendingMessages.isEmpty()) {
            return;
        }

        List<RecordId> reclaimCandidates = new ArrayList<>();
        int reclaimedCount = 0;

        for (PendingMessage pendingMessage : pendingMessages) {
            Duration elapsed = pendingMessage.getElapsedTimeSinceLastDelivery();
            if (elapsed == null || elapsed.toMillis() < interactionStreamProperties.getReclaimIdleMs()) {
                continue;
            }

            if (pendingMessage.getTotalDeliveryCount() >= interactionStreamProperties.getMaxDeliveries()) {
                movePendingMessageToDlq(pendingMessage);
                continue;
            }

            reclaimCandidates.add(pendingMessage.getId());
        }

        if (!reclaimCandidates.isEmpty()) {
            List<MapRecord<String, Object, Object>> claimedRecords = stringRedisTemplate.opsForStream().claim(
                    InteractionRedisKeys.SOCIAL_INTERACTION_STREAM_KEY,
                    interactionStreamProperties.getGroup(),
                    interactionStreamProperties.getConsumer(),
                    Duration.ofMillis(interactionStreamProperties.getReclaimIdleMs()),
                    reclaimCandidates.toArray(RecordId[]::new)
            );
            reclaimedCount = claimedRecords == null ? 0 : claimedRecords.size();
        }

        deadLetterService.logReclaimed(reclaimedCount);
    }

    private void movePendingMessageToDlq(PendingMessage pendingMessage) {
        MapRecord<String, Object, Object> record = loadRecord(pendingMessage.getId());
        if (record == null) {
            acknowledge(pendingMessage.getId());
            return;
        }

        deadLetterService.sendToDeadLetter(
                record,
                "max-deliveries-exceeded",
                pendingMessage.getTotalDeliveryCount(),
                pendingMessage.getConsumerName()
        );
        acknowledge(pendingMessage.getId());
    }

    private MapRecord<String, Object, Object> loadRecord(RecordId recordId) {
        List<MapRecord<String, Object, Object>> records = stringRedisTemplate.opsForStream().range(
                InteractionRedisKeys.SOCIAL_INTERACTION_STREAM_KEY,
                Range.closed(recordId.getValue(), recordId.getValue())
        );
        if (records == null || records.isEmpty()) {
            return null;
        }
        return records.get(0);
    }
    
    @SuppressWarnings("null")
    private void acknowledge(RecordId recordId) {
        stringRedisTemplate.opsForStream().acknowledge(
                InteractionRedisKeys.SOCIAL_INTERACTION_STREAM_KEY,
                interactionStreamProperties.getGroup(),
                recordId
        );
    }
}
