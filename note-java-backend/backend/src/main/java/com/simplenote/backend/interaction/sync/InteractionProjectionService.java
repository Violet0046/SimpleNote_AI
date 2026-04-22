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
public class InteractionProjectionService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private UserLikesMapper userLikesMapper;

    @Autowired
    private FollowMapper followMapper;

    @Autowired
    private PostMapper postMapper;

    @Transactional
    public void syncPostLikeProjection(Integer postId) {
        List<Integer> userIds = readIntegerSetMembers(InteractionRedisKeys.buildPostLikeSetKey(postId));

        if (userIds.isEmpty()) {
            userLikesMapper.deleteByPostId(postId);
            postMapper.updateLikesCount(postId, 0);
            return;
        }

        userLikesMapper.deleteByPostIdAndUserIdNotIn(postId, userIds);
        userLikesMapper.batchInsertByPostId(postId, userIds);
        postMapper.updateLikesCount(postId, userIds.size());
    }

    @Transactional
    public void syncFollowProjection(Integer followerId) {
        List<Integer> followedIds = readAllZSetMembers(InteractionRedisKeys.buildFollowingKey(followerId));

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
