package com.simplenote.backend.service.support;

import com.simplenote.backend.mapper.FollowMapper;
import com.simplenote.backend.mapper.PostMapper;
import com.simplenote.backend.mapper.UserLikesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
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

    private static final String USER_LIKED_POSTS_KEY = "user:likes:posts:";
    private static final String USER_LIKED_POSTS_INIT_KEY = "user:likes:init:";

    private static final String USER_FOLLOWING_KEY = "user:following:";
    private static final String USER_FOLLOWING_INIT_KEY = "user:following:init:";
    private static final String USER_FOLLOWING_DIRTY_KEY = "user:following:dirty";

    private static final String USER_FOLLOWERS_KEY = "user:followers:";
    private static final String USER_FOLLOWERS_INIT_KEY = "user:followers:init:";

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
        ensureUserLikedPostsCacheInitialized(userId);

        String postLikeSetKey = buildPostLikeSetKey(postId);
        String userLikedPostsKey = buildUserLikedPostsKey(userId);
        String userIdValue = toRedisValue(userId);
        String postIdValue = toRedisValue(postId);
        double score = System.currentTimeMillis();

        boolean liked = !Boolean.TRUE.equals(stringRedisTemplate.opsForSet().isMember(postLikeSetKey, userIdValue));
        if (liked) {
            stringRedisTemplate.opsForSet().add(postLikeSetKey, userIdValue);
            stringRedisTemplate.opsForZSet().add(userLikedPostsKey, postIdValue, score);
        } else {
            stringRedisTemplate.opsForSet().remove(postLikeSetKey, userIdValue);
            stringRedisTemplate.opsForZSet().remove(userLikedPostsKey, postIdValue);
        }

        stringRedisTemplate.opsForSet().add(POST_LIKE_DIRTY_KEY, postIdValue);
        return liked;
    }

    public Integer getCachedLikeCount(Integer postId) {
        if (!isPostLikeCacheInitialized(postId)) {
            return null;
        }
        Long size = stringRedisTemplate.opsForSet().size(buildPostLikeSetKey(postId));
        return size == null ? 0 : Math.toIntExact(size);
    }

    public Boolean getCachedLikeStatus(Integer postId, Integer userId) {
        if (userId == null || !isPostLikeCacheInitialized(postId)) {
            return null;
        }
        return Boolean.TRUE.equals(
                stringRedisTemplate.opsForSet().isMember(buildPostLikeSetKey(postId), toRedisValue(userId))
        );
    }

    public long getLikedPostCount(Integer userId) {
        ensureUserLikedPostsCacheInitialized(userId);
        Long size = stringRedisTemplate.opsForZSet().zCard(buildUserLikedPostsKey(userId));
        return size == null ? 0L : size;
    }

    public List<Integer> getLikedPostIdsPage(Integer userId, Integer pageNum, Integer pageSize) {
        ensureUserLikedPostsCacheInitialized(userId);
        return readPagedZSetMembers(buildUserLikedPostsKey(userId), pageNum, pageSize);
    }

    public boolean toggleFollow(Integer followerId, Integer followedId) {
        ensureFollowingCacheInitialized(followerId);
        ensureFollowersCacheInitialized(followedId);

        String followingKey = buildFollowingKey(followerId);
        String followersKey = buildFollowersKey(followedId);
        String followedIdValue = toRedisValue(followedId);
        String followerIdValue = toRedisValue(followerId);
        double score = System.currentTimeMillis();

        boolean following = stringRedisTemplate.opsForZSet().score(followingKey, followedIdValue) == null;
        if (following) {
            stringRedisTemplate.opsForZSet().add(followingKey, followedIdValue, score);
            stringRedisTemplate.opsForZSet().add(followersKey, followerIdValue, score);
        } else {
            stringRedisTemplate.opsForZSet().remove(followingKey, followedIdValue);
            stringRedisTemplate.opsForZSet().remove(followersKey, followerIdValue);
        }

        stringRedisTemplate.opsForSet().add(USER_FOLLOWING_DIRTY_KEY, followerIdValue);
        return following;
    }

    public boolean getFollowStatus(Integer followerId, Integer followedId) {
        ensureFollowingCacheInitialized(followerId);
        return stringRedisTemplate.opsForZSet().score(buildFollowingKey(followerId), toRedisValue(followedId)) != null;
    }

    public long getFollowingCount(Integer userId) {
        ensureFollowingCacheInitialized(userId);
        Long size = stringRedisTemplate.opsForZSet().zCard(buildFollowingKey(userId));
        return size == null ? 0L : size;
    }

    public long getFollowersCount(Integer userId) {
        ensureFollowersCacheInitialized(userId);
        Long size = stringRedisTemplate.opsForZSet().zCard(buildFollowersKey(userId));
        return size == null ? 0L : size;
    }

    public List<Integer> getFollowingIdsPage(Integer userId, Integer pageNum, Integer pageSize) {
        ensureFollowingCacheInitialized(userId);
        return readPagedZSetMembers(buildFollowingKey(userId), pageNum, pageSize);
    }

    public List<Integer> getFollowerIdsPage(Integer userId, Integer pageNum, Integer pageSize) {
        ensureFollowersCacheInitialized(userId);
        return readPagedZSetMembers(buildFollowersKey(userId), pageNum, pageSize);
    }

    @Transactional
    public void syncDirtyPostLikes() {
        String dirtyPostId;
        while ((dirtyPostId = stringRedisTemplate.opsForSet().pop(POST_LIKE_DIRTY_KEY)) != null) {
            Integer postId = Integer.valueOf(dirtyPostId);
            List<Integer> userIds = readIntegerSetMembers(buildPostLikeSetKey(postId));

            syncPostLikesToDatabase(postId, userIds);
            postMapper.updateLikesCount(postId, userIds.size());
        }
    }

    @Transactional
    public void syncDirtyFollows() {
        String dirtyFollowerId;
        while ((dirtyFollowerId = stringRedisTemplate.opsForSet().pop(USER_FOLLOWING_DIRTY_KEY)) != null) {
            Integer followerId = Integer.valueOf(dirtyFollowerId);
            List<Integer> followedIds = readAllZSetMembers(buildFollowingKey(followerId));

            syncFollowsToDatabase(followerId, followedIds);
        }
    }

    private void syncPostLikesToDatabase(Integer postId, List<Integer> userIds) {
        if (userIds.isEmpty()) {
            userLikesMapper.deleteByPostId(postId);
            return;
        }

        userLikesMapper.deleteByPostIdAndUserIdNotIn(postId, userIds);
        userLikesMapper.batchInsertByPostId(postId, userIds);
    }

    private void syncFollowsToDatabase(Integer followerId, List<Integer> followedIds) {
        if (followedIds.isEmpty()) {
            followMapper.deleteByFollowerId(followerId);
            return;
        }

        followMapper.deleteByFollowerIdAndFollowedIdNotIn(followerId, followedIds);
        followMapper.batchInsertByFollowerId(followerId, followedIds);
    }

    private void ensurePostLikeCacheInitialized(Integer postId) {
        String initKey = buildPostLikeInitKey(postId);
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(initKey))) {
            return;
        }
        synchronized (getLock(initKey)) {
            if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(initKey))) {
                return;
            }

            String setKey = buildPostLikeSetKey(postId);
            stringRedisTemplate.delete(setKey);
            List<Integer> userIds = userLikesMapper.findUserIdsByPostId(postId);
            if (!userIds.isEmpty()) {
                stringRedisTemplate.opsForSet().add(setKey, toArray(userIds));
            }
            stringRedisTemplate.opsForValue().set(initKey, "1");
        }
    }

    private void ensureUserLikedPostsCacheInitialized(Integer userId) {
        String initKey = buildUserLikedPostsInitKey(userId);
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(initKey))) {
            return;
        }
        synchronized (getLock(initKey)) {
            if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(initKey))) {
                return;
            }

            String zSetKey = buildUserLikedPostsKey(userId);
            stringRedisTemplate.delete(zSetKey);
            List<Integer> postIds = userLikesMapper.findPostIdsByUserId(userId);
            seedOrderedZSet(zSetKey, postIds);
            stringRedisTemplate.opsForValue().set(initKey, "1");
        }
    }

    private void ensureFollowingCacheInitialized(Integer followerId) {
        String initKey = buildFollowingInitKey(followerId);
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(initKey))) {
            return;
        }
        synchronized (getLock(initKey)) {
            if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(initKey))) {
                return;
            }

            String zSetKey = buildFollowingKey(followerId);
            stringRedisTemplate.delete(zSetKey);
            List<Integer> followedIds = followMapper.findFollowedIdsByFollowerId(followerId);
            seedOrderedZSet(zSetKey, followedIds);
            stringRedisTemplate.opsForValue().set(initKey, "1");
        }
    }

    private void ensureFollowersCacheInitialized(Integer followedId) {
        String initKey = buildFollowersInitKey(followedId);
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(initKey))) {
            return;
        }
        synchronized (getLock(initKey)) {
            if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(initKey))) {
                return;
            }

            String zSetKey = buildFollowersKey(followedId);
            stringRedisTemplate.delete(zSetKey);
            List<Integer> followerIds = followMapper.findFollowerIdsByFollowedId(followedId);
            seedOrderedZSet(zSetKey, followerIds);
            stringRedisTemplate.opsForValue().set(initKey, "1");
        }
    }

    private boolean isPostLikeCacheInitialized(Integer postId) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(buildPostLikeInitKey(postId)));
    }

    private List<Integer> readPagedZSetMembers(@NonNull String key, Integer pageNum, Integer pageSize) {
        long start = (long) (pageNum - 1) * pageSize;
        long end = start + pageSize - 1;
        Set<String> members = stringRedisTemplate.opsForZSet().reverseRange(key, start, end);
        return toIntegerList(members);
    }

    private List<Integer> readAllZSetMembers(@NonNull String key) {
        Set<String> members = stringRedisTemplate.opsForZSet().range(key, 0, -1);
        return toIntegerList(members);
    }

    private List<Integer> readIntegerSetMembers(@NonNull String key) {
        Set<String> members = stringRedisTemplate.opsForSet().members(key);
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

    private void seedOrderedZSet(@NonNull String key, List<Integer> memberIds) {
        if (memberIds == null || memberIds.isEmpty()) {
            return;
        }

        double score = System.currentTimeMillis();
        ZSetOperations<String, String> zSetOperations = stringRedisTemplate.opsForZSet();
        for (Integer memberId : memberIds) {
            zSetOperations.add(key, toRedisValue(memberId), score--);
        }
    }

    @NonNull
    private String[] toArray(List<Integer> values) {
        String[] array = new String[values.size()];
        for (int i = 0; i < values.size(); i++) {
            array[i] = toRedisValue(values.get(i));
        }
        return array;
    }

    @NonNull
    private String toRedisValue(Integer value) {
        return Objects.requireNonNull(String.valueOf(value));
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
    private String buildUserLikedPostsKey(Integer userId) {
        return USER_LIKED_POSTS_KEY + userId;
    }

    @NonNull
    private String buildUserLikedPostsInitKey(Integer userId) {
        return USER_LIKED_POSTS_INIT_KEY + userId;
    }

    @NonNull
    private String buildFollowingKey(Integer followerId) {
        return USER_FOLLOWING_KEY + followerId;
    }

    @NonNull
    private String buildFollowingInitKey(Integer followerId) {
        return USER_FOLLOWING_INIT_KEY + followerId;
    }

    @NonNull
    private String buildFollowersKey(Integer followedId) {
        return USER_FOLLOWERS_KEY + followedId;
    }

    @NonNull
    private String buildFollowersInitKey(Integer followedId) {
        return USER_FOLLOWERS_INIT_KEY + followedId;
    }
}
