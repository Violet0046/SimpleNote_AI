package com.simplenote.backend.interaction.redis;

import com.simplenote.backend.interaction.InteractionRedisKeys;
import com.simplenote.backend.pojo.LikeStateVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
public class InteractionRedisLikeService {
    private static final String POST_LIKE_EVENT_TYPE = "POST_LIKE_SET";

    private static final RedisScript<String> SET_POST_LIKE_STATE_SCRIPT = buildStringScript("""
            local currentlyLiked = redis.call('SISMEMBER', KEYS[1], ARGV[1]) == 1
            local desiredLiked = ARGV[3] == '1'

            if currentlyLiked == desiredLiked then
                local count = redis.call('SCARD', KEYS[1])
                return (desiredLiked and '1' or '0') .. '|0|' .. tostring(count)
            end

            if desiredLiked then
                redis.call('SADD', KEYS[1], ARGV[1])
                redis.call('ZADD', KEYS[2], ARGV[2], ARGV[4])
            else
                redis.call('SREM', KEYS[1], ARGV[1])
                redis.call('ZREM', KEYS[2], ARGV[4])
            end

            redis.call('SADD', KEYS[3], ARGV[4])
            redis.call(
                'XADD', KEYS[4], '*',
                'eventType', ARGV[6],
                'postId', ARGV[4],
                'userId', ARGV[1],
                'desiredState', ARGV[3],
                'occurredAt', ARGV[5]
            )

            local count = redis.call('SCARD', KEYS[1])
            return (desiredLiked and '1' or '0') .. '|1|' .. tostring(count)
            """);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private InteractionCacheInitService interactionCacheInitService;

    public LikeStateVO setPostLikeState(Integer postId, Integer userId, boolean desiredLiked) {
        interactionCacheInitService.ensurePostLikeCacheInitialized(postId);
        interactionCacheInitService.ensureUserLikedPostsCacheInitialized(userId);

        String postLikeSetKey = InteractionRedisKeys.buildPostLikeSetKey(postId);
        String userLikedPostsKey = InteractionRedisKeys.buildUserLikedPostsKey(userId);
        String userIdValue = InteractionRedisKeys.toRedisValue(userId);
        String postIdValue = InteractionRedisKeys.toRedisValue(postId);
        long now = System.currentTimeMillis();

        String scriptResult = stringRedisTemplate.execute(
                SET_POST_LIKE_STATE_SCRIPT,
                List.of(
                        postLikeSetKey,
                        userLikedPostsKey,
                        InteractionRedisKeys.POST_LIKE_DIRTY_KEY,
                        InteractionRedisKeys.SOCIAL_INTERACTION_STREAM_KEY
                ),
                userIdValue,
                String.valueOf(now),
                desiredLiked ? "1" : "0",
                postIdValue,
                String.valueOf(now),
                POST_LIKE_EVENT_TYPE
        );

        return parseLikeStateResult(scriptResult, "post like");
    }

    public Integer getCachedLikeCount(Integer postId) {
        if (!interactionCacheInitService.isPostLikeCacheInitialized(postId)) {
            return null;
        }
        Long size = stringRedisTemplate.opsForSet().size(InteractionRedisKeys.buildPostLikeSetKey(postId));
        return size == null ? 0 : Math.toIntExact(size);
    }

    public Boolean getCachedLikeStatus(Integer postId, Integer userId) {
        if (userId == null || !interactionCacheInitService.isPostLikeCacheInitialized(postId)) {
            return null;
        }
        return Boolean.TRUE.equals(
                stringRedisTemplate.opsForSet().isMember(
                        InteractionRedisKeys.buildPostLikeSetKey(postId),
                        InteractionRedisKeys.toRedisValue(userId)
                )
        );
    }

    public long getLikedPostCount(Integer userId) {
        interactionCacheInitService.ensureUserLikedPostsCacheInitialized(userId);
        Long size = stringRedisTemplate.opsForZSet().zCard(InteractionRedisKeys.buildUserLikedPostsKey(userId));
        return size == null ? 0L : size;
    }

    public List<Integer> getLikedPostIdsPage(Integer userId, Integer pageNum, Integer pageSize) {
        interactionCacheInitService.ensureUserLikedPostsCacheInitialized(userId);
        return readPagedZSetMembers(InteractionRedisKeys.buildUserLikedPostsKey(userId), pageNum, pageSize);
    }

    @NonNull
    private LikeStateVO parseLikeStateResult(String scriptResult, String action) {
        String[] parts = requireScriptParts(scriptResult, 3, action);
        boolean liked = "1".equals(parts[0]);
        boolean changed = "1".equals(parts[1]);
        Integer likeCount = Integer.valueOf(parts[2]);
        return new LikeStateVO(liked, changed, likeCount);
    }

    @NonNull
    private String[] requireScriptParts(String scriptResult, int expectedLength, String action) {
        if (scriptResult == null || scriptResult.isBlank()) {
            throw new IllegalStateException("Redis script returned empty result for " + action);
        }

        String[] parts = scriptResult.split("\\|");
        if (parts.length != expectedLength) {
            throw new IllegalStateException("Unexpected Redis script result for " + action + ": " + scriptResult);
        }

        return parts;
    }

    private List<Integer> readPagedZSetMembers(@NonNull String key, Integer pageNum, Integer pageSize) {
        long start = (long) (pageNum - 1) * pageSize;
        long end = start + pageSize - 1;
        Set<String> members = stringRedisTemplate.opsForZSet().reverseRange(key, start, end);
        return toIntegerList(members);
    }

    private List<Integer> toIntegerList(Set<String> members) {
        if (members == null || members.isEmpty()) {
            return Collections.emptyList();
        }

        List<Integer> result = new ArrayList<>(members.size());
        for (String member : members) {
            result.add(Integer.valueOf(member));
        }
        return result;
    }

    @NonNull
    private static RedisScript<String> buildStringScript(String scriptText) {
        DefaultRedisScript<String> script = new DefaultRedisScript<>();
        script.setScriptText(scriptText);
        script.setResultType(String.class);
        return script;
    }
}
