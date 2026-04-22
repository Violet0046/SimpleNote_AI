package com.simplenote.backend.interaction.sync;

import com.simplenote.backend.interaction.InteractionRedisKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Service
public class InteractionSyncService {
    private static final long DIRTY_SCAN_COUNT = 100L;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private InteractionProjectionService interactionProjectionService;

    @SuppressWarnings("null")
    public void syncDirtyPostLikes() {
        processDirtyEntities(
                InteractionRedisKeys.POST_LIKE_DIRTY_KEY,
                InteractionRedisKeys.POST_LIKE_PROCESSING_KEY,
                dirtyPostId -> interactionProjectionService.syncPostLikeProjection(Integer.valueOf(dirtyPostId))
        );
    }

    @SuppressWarnings("null")
    public void syncDirtyFollows() {
        processDirtyEntities(
                InteractionRedisKeys.USER_FOLLOWING_DIRTY_KEY,
                InteractionRedisKeys.USER_FOLLOWING_PROCESSING_KEY,
                dirtyFollowerId -> interactionProjectionService.syncFollowProjection(Integer.valueOf(dirtyFollowerId))
        );
    }

    @SuppressWarnings("null")
    private void processDirtyEntities(String dirtyKey, String processingKey, Consumer<String> projector) {
        for (String dirtyEntityId : scanSetMembers(dirtyKey)) {
            boolean claimed = Boolean.TRUE.equals(
                    stringRedisTemplate.opsForSet().move(dirtyKey, dirtyEntityId, processingKey)
            );
            if (!claimed) {
                continue;
            }

            try {
                projector.accept(dirtyEntityId);
                stringRedisTemplate.opsForSet().remove(processingKey, dirtyEntityId);
            } catch (RuntimeException ex) {
                stringRedisTemplate.opsForSet().move(processingKey, dirtyEntityId, dirtyKey);
                throw ex;
            }
        }
    }

    @SuppressWarnings("null")
    @NonNull
    private List<String> scanSetMembers(String key) {
        List<String> members = new ArrayList<>();
        ScanOptions options = ScanOptions.scanOptions().count(DIRTY_SCAN_COUNT).build();

        try (Cursor<String> cursor = stringRedisTemplate.opsForSet().scan(key, options)) {
            while (cursor.hasNext()) {
                members.add(cursor.next());
            }
        }

        return members;
    }
}
