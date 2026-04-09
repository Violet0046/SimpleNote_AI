package com.simplenote.backend.service.support;

import com.simplenote.backend.mapper.FollowMapper;
import com.simplenote.backend.mapper.PostMapper;
import com.simplenote.backend.mapper.UserLikesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InteractionRedisService {
    private static final String POST_LIKE_SET_KEY = "post:likes:users:";
    private static final String POST_LIKE_INIT_KEY = "post:likes:init:";
    private static final String POST_LIKE_DIRTY_KEY = "post:likes:dirty";

    private static final String USER_FOLLOWING_SET_KEY = "user:following:";
    private static final String USER_FOLLOWING_INIT_KEY = "user:following:init:";
    private static final String USER_FOLLOWING_DIRTY_KEY = "user:following:dirty";

    private final ConcurrentHashMap<String, Object> initLocks = new ConcurrentHashMap<>();

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private UserLikesMapper userLikesMapper;

    @Autowired
    private FollowMapper followMapper;

    @Autowired
    private PostMapper postMapper;

    public boolean togglePostLike(Integer postId, Integer userId) {
        ensurePostLikeCacheInitialized(postId);
        String setKey = Objects.requireNonNull(buildPostLikeSetKey(postId));
        String userIdValue = Objects.requireNonNull(String.valueOf(userId));

        boolean liked = !Boolean.TRUE.equals(stringRedisTemplate.opsForSet().isMember(setKey, userIdValue));
        if (liked) {
            stringRedisTemplate.opsForSet().add(setKey, userIdValue);
        } else {
            stringRedisTemplate.opsForSet().remove(setKey, userIdValue);
        }
        String dirtyPostId = Objects.requireNonNull(String.valueOf(postId));
        stringRedisTemplate.opsForSet().add(POST_LIKE_DIRTY_KEY, dirtyPostId);
        return liked;
    }

    public Integer getCachedLikeCount(Integer postId) {
        if (!isPostLikeCacheInitialized(postId)) {
            return null;
        }
        String setKey = Objects.requireNonNull(buildPostLikeSetKey(postId));
        Long size = stringRedisTemplate.opsForSet().size(setKey);
        return size == null ? 0 : Math.toIntExact(size);
    }

    public Boolean getCachedLikeStatus(Integer postId, Integer userId) {
        if (userId == null || !isPostLikeCacheInitialized(postId)) {
            return null;
        }
        String setKey = Objects.requireNonNull(buildPostLikeSetKey(postId));
        String userIdValue = Objects.requireNonNull(String.valueOf(userId));
        return Boolean.TRUE.equals(
                stringRedisTemplate.opsForSet().isMember(setKey, userIdValue)
        );
    }

    public boolean toggleFollow(Integer followerId, Integer followedId) {
        ensureFollowingCacheInitialized(followerId);
        String setKey = Objects.requireNonNull(buildFollowingSetKey(followerId));
        String followedIdValue = Objects.requireNonNull(String.valueOf(followedId));

        boolean following = !Boolean.TRUE.equals(stringRedisTemplate.opsForSet().isMember(setKey, followedIdValue));
        if (following) {
            stringRedisTemplate.opsForSet().add(setKey, followedIdValue);
        } else {
            stringRedisTemplate.opsForSet().remove(setKey, followedIdValue);
        }
        String dirtyFollowerId = Objects.requireNonNull(String.valueOf(followerId));
        stringRedisTemplate.opsForSet().add(USER_FOLLOWING_DIRTY_KEY, dirtyFollowerId);
        return following;
    }

    public Boolean getCachedFollowStatus(Integer followerId, Integer followedId) {
        if (!isFollowingCacheInitialized(followerId)) {
            return null;
        }
        String setKey = Objects.requireNonNull(buildFollowingSetKey(followerId));
        String followedIdValue = Objects.requireNonNull(String.valueOf(followedId));
        return Boolean.TRUE.equals(
                stringRedisTemplate.opsForSet().isMember(setKey, followedIdValue)
        );
    }

    @Transactional
    public void syncDirtyPostLikes() {
        String dirtyPostId;
        while ((dirtyPostId = stringRedisTemplate.opsForSet().pop(POST_LIKE_DIRTY_KEY)) != null) {
            Integer postId = Integer.valueOf(dirtyPostId);
            List<Integer> userIds = readIntegerMembers(buildPostLikeSetKey(postId));

            userLikesMapper.deleteByPostId(postId);
            if (!userIds.isEmpty()) {
                userLikesMapper.batchInsertByPostId(postId, userIds);
            }
            postMapper.updateLikesCount(postId, userIds.size());
        }
    }

    @Transactional
    public void syncDirtyFollows() {
        String dirtyFollowerId;
        while ((dirtyFollowerId = stringRedisTemplate.opsForSet().pop(USER_FOLLOWING_DIRTY_KEY)) != null) {
            Integer followerId = Integer.valueOf(dirtyFollowerId);
            List<Integer> followedIds = readIntegerMembers(buildFollowingSetKey(followerId));

            followMapper.deleteByFollowerId(followerId);
            if (!followedIds.isEmpty()) {
                followMapper.batchInsertByFollowerId(followerId, followedIds);
            }
        }
    }

    private void ensurePostLikeCacheInitialized(Integer postId) {
        String initKey = Objects.requireNonNull(buildPostLikeInitKey(postId));
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(initKey))) {
            return;
        }
        synchronized (getLock(initKey)) {
            if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(initKey))) {
                return;
            }

            String setKey = Objects.requireNonNull(buildPostLikeSetKey(postId));
            stringRedisTemplate.delete(setKey);
            List<Integer> userIds = userLikesMapper.findUserIdsByPostId(postId);
            if (!userIds.isEmpty()) {
                stringRedisTemplate.opsForSet().add(setKey, toArray(userIds));
            }
            stringRedisTemplate.opsForValue().set(initKey, "1");
        }
    }

    private void ensureFollowingCacheInitialized(Integer followerId) {
        String initKey = Objects.requireNonNull(buildFollowingInitKey(followerId));
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(initKey))) {
            return;
        }
        synchronized (getLock(initKey)) {
            if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(initKey))) {
                return;
            }

            String setKey = Objects.requireNonNull(buildFollowingSetKey(followerId));
            stringRedisTemplate.delete(setKey);
            List<Integer> followedIds = followMapper.findFollowedIdsByFollowerId(followerId);
            if (!followedIds.isEmpty()) {
                stringRedisTemplate.opsForSet().add(setKey, toArray(followedIds));
            }
            stringRedisTemplate.opsForValue().set(initKey, "1");
        }
    }

    private boolean isPostLikeCacheInitialized(Integer postId) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(buildPostLikeInitKey(postId)));
    }

    private boolean isFollowingCacheInitialized(Integer followerId) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(buildFollowingInitKey(followerId)));
    }

    private List<Integer> readIntegerMembers(@NonNull String key) {
        Set<String> members = stringRedisTemplate.opsForSet().members(key);
        if (members == null || members.isEmpty()) {
            return Collections.emptyList();
        }

        List<Integer> result = new ArrayList<>(members.size());
        for (String member : members) {
            result.add(Integer.valueOf(member));
        }
        return result;
    }

    private @NonNull String[] toArray(List<Integer> values) {
        String[] array = new String[values.size()];
        for (int i = 0; i < values.size(); i++) {
            array[i] = String.valueOf(values.get(i));
        }
        return array;
    }

    private Object getLock(@NonNull String key) {
        return initLocks.computeIfAbsent(key, ignored -> new Object());
    }

    @NonNull
    private String buildPostLikeSetKey(Integer postId) {
        return POST_LIKE_SET_KEY + postId;
    }

    @NonNull
    private String buildPostLikeInitKey(Integer postId) {
        return POST_LIKE_INIT_KEY + postId;
    }

    @NonNull
    private String buildFollowingSetKey(Integer followerId) {
        return USER_FOLLOWING_SET_KEY + followerId;
    }

    @NonNull
    private String buildFollowingInitKey(Integer followerId) {
        return USER_FOLLOWING_INIT_KEY + followerId;
    }
}
