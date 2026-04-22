package com.simplenote.backend.interaction.stream;

import com.simplenote.backend.interaction.InteractionRedisKeys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class InteractionStreamGroupInitializer {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private InteractionStreamProperties interactionStreamProperties;

    @SuppressWarnings("null")
    @PostConstruct
    public void ensureConsumerGroup() {
        ensureStreamExists();

        try {
            stringRedisTemplate.opsForStream().createGroup(
                    InteractionRedisKeys.SOCIAL_INTERACTION_STREAM_KEY,
                    ReadOffset.latest(),
                    interactionStreamProperties.getGroup()
            );
        } catch (DataAccessException ex) {
            if (!isBusyGroupError(ex)) {
                throw ex;
            }
        }
    }

    @SuppressWarnings("null")
    private void ensureStreamExists() {
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(InteractionRedisKeys.SOCIAL_INTERACTION_STREAM_KEY))) {
            return;
        }

        stringRedisTemplate.opsForStream().add(
                StreamRecords.string(Map.of("bootstrap", "1"))
                        .withStreamKey(InteractionRedisKeys.SOCIAL_INTERACTION_STREAM_KEY)
        );
    }

    private boolean isBusyGroupError(DataAccessException ex) {
        Throwable current = ex;
        while (current != null) {
            String message = current.getMessage();
            if (message != null && message.contains("BUSYGROUP")) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }
}
