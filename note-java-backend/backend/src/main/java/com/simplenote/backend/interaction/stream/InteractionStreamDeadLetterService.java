package com.simplenote.backend.interaction.stream;

import com.simplenote.backend.interaction.InteractionRedisKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class InteractionStreamDeadLetterService {
    private static final Logger log = LoggerFactory.getLogger(InteractionStreamDeadLetterService.class);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public void sendToDeadLetter(MapRecord<String, Object, Object> record,
                                 String reason,
                                 long deliveryCount,
                                 String sourceConsumer) {
        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("originalStream", InteractionRedisKeys.SOCIAL_INTERACTION_STREAM_KEY);
        payload.put("originalRecordId", record.getId().getValue());
        payload.put("reason", reason);
        payload.put("deliveryCount", String.valueOf(deliveryCount));
        payload.put("sourceConsumer", sourceConsumer == null ? "" : sourceConsumer);
        payload.put("dlqAt", String.valueOf(System.currentTimeMillis()));

        record.getValue().forEach((key, value) -> payload.put(String.valueOf(key), String.valueOf(value)));

        RecordId dlqRecordId = stringRedisTemplate.opsForStream().add(
                StreamRecords.string(payload).withStreamKey(InteractionRedisKeys.SOCIAL_INTERACTION_DLQ_KEY)
        );

        log.warn(
                "Interaction stream message moved to DLQ. originalRecordId={}, dlqRecordId={}, reason={}, deliveryCount={}, sourceConsumer={}",
                record.getId().getValue(),
                dlqRecordId == null ? "null" : dlqRecordId.getValue(),
                reason,
                deliveryCount,
                sourceConsumer
        );
    }

    public void logProjectionFailure(String aggregateType, Integer aggregateId, Throwable throwable) {
        log.error(
                "Interaction stream projection failed. aggregateType={}, aggregateId={}, message={}",
                aggregateType,
                aggregateId,
                throwable.getMessage(),
                throwable
        );
    }

    public void logBatchConsumed(int recordCount, int uniquePostCount, int uniqueFollowCount) {
        log.info(
                "Interaction stream batch consumed. records={}, uniquePostAggregates={}, uniqueFollowAggregates={}",
                recordCount,
                uniquePostCount,
                uniqueFollowCount
        );
    }

    public void logReclaimed(int reclaimedCount) {
        if (reclaimedCount > 0) {
            log.info("Interaction stream pending messages reclaimed. count={}", reclaimedCount);
        }
    }
}
