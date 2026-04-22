package com.simplenote.backend.interaction.redis;

import com.simplenote.backend.interaction.InteractionRedisKeys;
import com.simplenote.backend.pojo.FollowStateVO;
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
public class InteractionRedisFollowService {
    private static final String FOLLOW_EVENT_TYPE = "FOLLOW_SET";

    @SuppressWarnings("null")
    private static final RedisScript<String> SET_FOLLOW_STATE_SCRIPT = buildStringScript("""
            local currentlyFollowing = redis.call('ZSCORE', KEYS[1], ARGV[2]) ~= false
            local desiredFollowing = ARGV[3] == '1'

            if currentlyFollowing == desiredFollowing then
                return (desiredFollowing and '1' or '0') .. '|0'
            end

            if desiredFollowing then
                redis.call('ZADD', KEYS[1], ARGV[4], ARGV[2])
                redis.call('ZADD', KEYS[2], ARGV[4], ARGV[1])
            else
                redis.call('ZREM', KEYS[1], ARGV[2])
                redis.call('ZREM', KEYS[2], ARGV[1])
            end

            redis.call('SADD', KEYS[3], ARGV[1])
            redis.call(
                'XADD', KEYS[4], '*',
                'eventType', ARGV[6],
                'followerId', ARGV[1],
                'followedId', ARGV[2],
                'desiredState', ARGV[3],
                'occurredAt', ARGV[5]
            )

            return (desiredFollowing and '1' or '0') .. '|1'
            """);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private InteractionCacheInitService interactionCacheInitService;

    @SuppressWarnings("null")
    public FollowStateVO setFollowState(Integer followerId, Integer followedId, boolean desiredFollowing) {
        interactionCacheInitService.ensureFollowingCacheInitialized(followerId);
        interactionCacheInitService.ensureFollowersCacheInitialized(followedId);

        String followingKey = InteractionRedisKeys.buildFollowingKey(followerId);
        String followersKey = InteractionRedisKeys.buildFollowersKey(followedId);
        String followedIdValue = InteractionRedisKeys.toRedisValue(followedId);
        String followerIdValue = InteractionRedisKeys.toRedisValue(followerId);
        long now = System.currentTimeMillis();

        String scriptResult = stringRedisTemplate.execute(
                SET_FOLLOW_STATE_SCRIPT,
                List.of(
                        followingKey,
                        followersKey,
                        InteractionRedisKeys.USER_FOLLOWING_DIRTY_KEY,
                        InteractionRedisKeys.SOCIAL_INTERACTION_STREAM_KEY
                ),
                followerIdValue,
                followedIdValue,
                desiredFollowing ? "1" : "0",
                String.valueOf(now),
                String.valueOf(now),
                FOLLOW_EVENT_TYPE
        );

        return parseFollowStateResult(scriptResult, "follow");
    }

    public boolean getFollowStatus(Integer followerId, Integer followedId) {
        interactionCacheInitService.ensureFollowingCacheInitialized(followerId);
        return stringRedisTemplate.opsForZSet().score(
                InteractionRedisKeys.buildFollowingKey(followerId),
                InteractionRedisKeys.toRedisValue(followedId)
        ) != null;
    }

    public long getFollowingCount(Integer userId) {
        interactionCacheInitService.ensureFollowingCacheInitialized(userId);
        Long size = stringRedisTemplate.opsForZSet().zCard(InteractionRedisKeys.buildFollowingKey(userId));
        return size == null ? 0L : size;
    }

    public long getFollowersCount(Integer userId) {
        interactionCacheInitService.ensureFollowersCacheInitialized(userId);
        Long size = stringRedisTemplate.opsForZSet().zCard(InteractionRedisKeys.buildFollowersKey(userId));
        return size == null ? 0L : size;
    }

    public List<Integer> getFollowingIdsPage(Integer userId, Integer pageNum, Integer pageSize) {
        interactionCacheInitService.ensureFollowingCacheInitialized(userId);
        return readPagedZSetMembers(InteractionRedisKeys.buildFollowingKey(userId), pageNum, pageSize);
    }

    public List<Integer> getFollowerIdsPage(Integer userId, Integer pageNum, Integer pageSize) {
        interactionCacheInitService.ensureFollowersCacheInitialized(userId);
        return readPagedZSetMembers(InteractionRedisKeys.buildFollowersKey(userId), pageNum, pageSize);
    }

    @NonNull
    private FollowStateVO parseFollowStateResult(String scriptResult, String action) {
        String[] parts = requireScriptParts(scriptResult, 2, action);
        boolean following = "1".equals(parts[0]);
        boolean changed = "1".equals(parts[1]);
        return new FollowStateVO(following, changed);
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

    @SuppressWarnings("null")
    @NonNull
    private static RedisScript<String> buildStringScript(String scriptText) {
        DefaultRedisScript<String> script = new DefaultRedisScript<>();
        script.setScriptText(scriptText);
        script.setResultType(String.class);
        return script;
    }
}
