package com.simplenote.backend.interaction.stream;

import com.simplenote.backend.interaction.InteractionRedisKeys;
import com.simplenote.backend.interaction.sync.InteractionProjectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.connection.stream.StreamReadOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class InteractionStreamConsumer {
    private static final String PENDING_START_OFFSET = "0-0";
    private static final String POST_LIKE_EVENT_TYPE = "POST_LIKE_SET";
    private static final String FOLLOW_EVENT_TYPE = "FOLLOW_SET";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private InteractionProjectionService interactionProjectionService;

    @Autowired
    private InteractionStreamProperties interactionStreamProperties;

    @Autowired
    private InteractionStreamDeadLetterService deadLetterService;

    @SuppressWarnings("null")
    @Scheduled(fixedDelayString = "${social.stream.poll-fixed-delay-ms}")
    public void consumeInteractionEvents() {
        boolean processedPending = consumeBatch(ReadOffset.from(PENDING_START_OFFSET));
        if (!processedPending) {
            consumeBatch(ReadOffset.lastConsumed());
        }
    }

    @SuppressWarnings({"null", "unchecked"})
    private boolean consumeBatch(ReadOffset readOffset) {
        List<MapRecord<String, Object, Object>> records = stringRedisTemplate.opsForStream().read(
                Consumer.from(
                        interactionStreamProperties.getGroup(),
                        interactionStreamProperties.getConsumer()
                ),
                StreamReadOptions.empty()
                        .count(interactionStreamProperties.getBatchSize())
                        .block(Duration.ofMillis(interactionStreamProperties.getBlockMs())),
                StreamOffset.create(InteractionRedisKeys.SOCIAL_INTERACTION_STREAM_KEY, readOffset)
        );

        if (records == null || records.isEmpty()) {
            return false;
        }

        Map<Integer, List<RecordId>> postLikeRecordIds = new LinkedHashMap<>();
        Map<Integer, List<RecordId>> followRecordIds = new LinkedHashMap<>();

        for (MapRecord<String, Object, Object> record : records) {
            routeRecord(record, postLikeRecordIds, followRecordIds);
        }

        acknowledgeProjectedPostLikes(postLikeRecordIds);
        acknowledgeProjectedFollows(followRecordIds);
        deadLetterService.logBatchConsumed(records.size(), postLikeRecordIds.size(), followRecordIds.size());
        return true;
    }

    @SuppressWarnings("null")
    private void acknowledgeProjectedPostLikes(Map<Integer, List<RecordId>> postLikeRecordIds) {
        for (Map.Entry<Integer, List<RecordId>> entry : postLikeRecordIds.entrySet()) {
            try {
                interactionProjectionService.syncPostLikeProjection(entry.getKey());
                acknowledge(entry.getValue());
            } catch (RuntimeException ex) {
                deadLetterService.logProjectionFailure("post-like", entry.getKey(), ex);
            }
        }
    }

    @SuppressWarnings("null")
    private void acknowledgeProjectedFollows(Map<Integer, List<RecordId>> followRecordIds) {
        for (Map.Entry<Integer, List<RecordId>> entry : followRecordIds.entrySet()) {
            try {
                interactionProjectionService.syncFollowProjection(entry.getKey());
                acknowledge(entry.getValue());
            } catch (RuntimeException ex) {
                deadLetterService.logProjectionFailure("follow", entry.getKey(), ex);
            }
        }
    }

    @SuppressWarnings("null")
    private void acknowledge(List<RecordId> recordIds) {
        if (recordIds.isEmpty()) {
            return;
        }

        stringRedisTemplate.opsForStream().acknowledge(
                InteractionRedisKeys.SOCIAL_INTERACTION_STREAM_KEY,
                interactionStreamProperties.getGroup(),
                recordIds.toArray(RecordId[]::new)
        );
    }

    @SuppressWarnings("null")
    private void routeRecord(MapRecord<String, Object, Object> record,
                             Map<Integer, List<RecordId>> postLikeRecordIds,
                             Map<Integer, List<RecordId>> followRecordIds) {
        try {
            String eventType = readRequiredValue(record, "eventType");
            RecordId recordId = record.getId();

            switch (eventType) {
                case POST_LIKE_EVENT_TYPE -> {
                    Integer postId = Integer.valueOf(readRequiredValue(record, "postId"));
                    postLikeRecordIds.computeIfAbsent(postId, key -> new ArrayList<>()).add(recordId);
                }
                case FOLLOW_EVENT_TYPE -> {
                    Integer followerId = Integer.valueOf(readRequiredValue(record, "followerId"));
                    followRecordIds.computeIfAbsent(followerId, key -> new ArrayList<>()).add(recordId);
                }
                default -> throw new IllegalStateException("Unsupported interaction stream event: " + eventType);
            }
        } catch (RuntimeException ex) {
            deadLetterService.sendToDeadLetter(record, ex.getMessage(), 1L, interactionStreamProperties.getConsumer());
            acknowledge(List.of(record.getId()));
        }
    }

    @NonNull
    private String readRequiredValue(MapRecord<String, Object, Object> record, String fieldName) {
        Object value = record.getValue().get(fieldName);
        if (value == null) {
            throw new IllegalStateException("Missing field '" + fieldName + "' in interaction stream record: " + record);
        }
        return Objects.requireNonNull(value.toString());
    }
}
