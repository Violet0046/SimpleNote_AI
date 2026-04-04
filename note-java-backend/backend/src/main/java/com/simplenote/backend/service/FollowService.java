package com.simplenote.backend.service;

import java.util.List;

import com.simplenote.backend.pojo.UserDetailVO;

public interface FollowService {
    // 切换关注状态，返回提示信息
    String toggleFollow(Integer followedId);

    List<UserDetailVO> getFollowingList(Integer userId);
    List<UserDetailVO> getFollowersList(Integer userId);

    // 检查关注状态
    boolean isFollowing(Integer followerId, Integer followedId);
}