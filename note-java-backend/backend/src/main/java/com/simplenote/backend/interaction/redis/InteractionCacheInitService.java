package com.simplenote.backend.interaction.redis;

import com.simplenote.backend.interaction.InteractionRedisKeys;
import com.simplenote.backend.mapper.FollowMapper;
import com.simplenote.backend.mapper.UserLikesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InteractionCacheInitService {
    private final ConcurrentHashMap<String, Object> initLocks = new ConcurrentHashMap<>();

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private UserLikesMapper userLikesMapper;

    @Autowired
    private FollowMapper followMapper;

    public void ensurePostLikeCacheInitialized(Integer postId) {
        String initKey = InteractionRedisKeys.buildPostLikeInitKey(postId);
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(initKey))) {
            return;
        }
        synchronized (getLock(initKey)) {
            if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(initKey))) {
                return;
            }

            String setKey = InteractionRedisKeys.buildPostLikeSetKey(postId);
            stringRedisTemplate.delete(setKey);
            List<Integer> userIds = userLikesMapper.findUserIdsByPostId(postId);
            if (!userIds.isEmpty()) {
                stringRedisTemplate.opsForSet().add(setKey, toArray(userIds));
            }
            stringRedisTemplate.opsForValue().set(initKey, "1");
        }
    }

    public void ensureUserLikedPostsCacheInitialized(Integer userId) {
        String initKey = InteractionRedisKeys.buildUserLikedPostsInitKey(userId);
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(initKey))) {
            return;
        }
        synchronized (getLock(initKey)) {
            if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(initKey))) {
                return;
            }

            String zSetKey = InteractionRedisKeys.buildUserLikedPostsKey(userId);
            stringRedisTemplate.delete(zSetKey);
            List<Integer> postIds = userLikesMapper.findPostIdsByUserId(userId);
            seedOrderedZSet(zSetKey, postIds);
            stringRedisTemplate.opsForValue().set(initKey, "1");
        }
    }

    public void ensureFollowingCacheInitialized(Integer followerId) {
        String initKey = InteractionRedisKeys.buildFollowingInitKey(followerId);
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(initKey))) {
            return;
        }
        synchronized (getLock(initKey)) {
            if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(initKey))) {
                return;
            }

            String zSetKey = InteractionRedisKeys.buildFollowingKey(followerId);
            stringRedisTemplate.delete(zSetKey);
            List<Integer> followedIds = followMapper.findFollowedIdsByFollowerId(followerId);
            seedOrderedZSet(zSetKey, followedIds);
            stringRedisTemplate.opsForValue().set(initKey, "1");
        }
    }

    public void ensureFollowersCacheInitialized(Integer followedId) {
        String initKey = InteractionRedisKeys.buildFollowersInitKey(followedId);
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(initKey))) {
            return;
        }
        synchronized (getLock(initKey)) {
            if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(initKey))) {
                return;
            }

            String zSetKey = InteractionRedisKeys.buildFollowersKey(followedId);
            stringRedisTemplate.delete(zSetKey);
            List<Integer> followerIds = followMapper.findFollowerIdsByFollowedId(followedId);
            seedOrderedZSet(zSetKey, followerIds);
            stringRedisTemplate.opsForValue().set(initKey, "1");
        }
    }

    public boolean isPostLikeCacheInitialized(Integer postId) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(InteractionRedisKeys.buildPostLikeInitKey(postId)));
    }

    private void seedOrderedZSet(@NonNull String key, List<Integer> memberIds) {
        if (memberIds == null || memberIds.isEmpty()) {
            return;
        }

        double score = System.currentTimeMillis();
        ZSetOperations<String, String> zSetOperations = stringRedisTemplate.opsForZSet();
        for (Integer memberId : memberIds) {
            zSetOperations.add(key, InteractionRedisKeys.toRedisValue(memberId), score--);
        }
    }

    @NonNull
    private String[] toArray(List<Integer> values) {
        String[] array = new String[values.size()];
        for (int i = 0; i < values.size(); i++) {
            array[i] = InteractionRedisKeys.toRedisValue(values.get(i));
        }
        return array;
    }

    private Object getLock(@NonNull String key) {
        return initLocks.computeIfAbsent(key, ignored -> new Object());
    }
}
