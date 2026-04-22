package com.simplenote.backend.interaction;

import com.simplenote.backend.interaction.redis.InteractionRedisFollowService;
import com.simplenote.backend.interaction.redis.InteractionRedisLikeService;
import com.simplenote.backend.interaction.sync.InteractionSyncService;
import com.simplenote.backend.pojo.FollowStateVO;
import com.simplenote.backend.pojo.LikeStateVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InteractionRedisService {
    @Autowired
    private InteractionRedisLikeService interactionRedisLikeService;

    @Autowired
    private InteractionRedisFollowService interactionRedisFollowService;

    @Autowired
    private InteractionSyncService interactionSyncService;

    public LikeStateVO setPostLikeState(Integer postId, Integer userId, boolean desiredLiked) {
        return interactionRedisLikeService.setPostLikeState(postId, userId, desiredLiked);
    }

    public Integer getCachedLikeCount(Integer postId) {
        return interactionRedisLikeService.getCachedLikeCount(postId);
    }

    public Boolean getCachedLikeStatus(Integer postId, Integer userId) {
        return interactionRedisLikeService.getCachedLikeStatus(postId, userId);
    }

    public long getLikedPostCount(Integer userId) {
        return interactionRedisLikeService.getLikedPostCount(userId);
    }

    public List<Integer> getLikedPostIdsPage(Integer userId, Integer pageNum, Integer pageSize) {
        return interactionRedisLikeService.getLikedPostIdsPage(userId, pageNum, pageSize);
    }

    public FollowStateVO setFollowState(Integer followerId, Integer followedId, boolean desiredFollowing) {
        return interactionRedisFollowService.setFollowState(followerId, followedId, desiredFollowing);
    }

    public boolean getFollowStatus(Integer followerId, Integer followedId) {
        return interactionRedisFollowService.getFollowStatus(followerId, followedId);
    }

    public long getFollowingCount(Integer userId) {
        return interactionRedisFollowService.getFollowingCount(userId);
    }

    public long getFollowersCount(Integer userId) {
        return interactionRedisFollowService.getFollowersCount(userId);
    }

    public List<Integer> getFollowingIdsPage(Integer userId, Integer pageNum, Integer pageSize) {
        return interactionRedisFollowService.getFollowingIdsPage(userId, pageNum, pageSize);
    }

    public List<Integer> getFollowerIdsPage(Integer userId, Integer pageNum, Integer pageSize) {
        return interactionRedisFollowService.getFollowerIdsPage(userId, pageNum, pageSize);
    }

    public void syncDirtyPostLikes() {
        interactionSyncService.syncDirtyPostLikes();
    }

    public void syncDirtyFollows() {
        interactionSyncService.syncDirtyFollows();
    }
}
