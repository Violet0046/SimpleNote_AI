package com.simplenote.backend.interaction.sync;

import com.simplenote.backend.interaction.InteractionRedisKeys;
import com.simplenote.backend.mapper.FollowMapper;
import com.simplenote.backend.mapper.PostMapper;
import com.simplenote.backend.mapper.UserLikesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
public class InteractionSyncService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private UserLikesMapper userLikesMapper;

    @Autowired
    private FollowMapper followMapper;

    @Autowired
    private PostMapper postMapper;

    @Transactional
    public void syncDirtyPostLikes() {
        String dirtyPostId;
        while ((dirtyPostId = stringRedisTemplate.opsForSet().pop(InteractionRedisKeys.POST_LIKE_DIRTY_KEY)) != null) {
            Integer postId = Integer.valueOf(dirtyPostId);
            List<Integer> userIds = readIntegerSetMembers(InteractionRedisKeys.buildPostLikeSetKey(postId));

            syncPostLikesToDatabase(postId, userIds);
            postMapper.updateLikesCount(postId, userIds.size());
        }
    }

    @Transactional
    public void syncDirtyFollows() {
        String dirtyFollowerId;
        while ((dirtyFollowerId = stringRedisTemplate.opsForSet().pop(InteractionRedisKeys.USER_FOLLOWING_DIRTY_KEY)) != null) {
            Integer followerId = Integer.valueOf(dirtyFollowerId);
            List<Integer> followedIds = readAllZSetMembers(InteractionRedisKeys.buildFollowingKey(followerId));

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
}
