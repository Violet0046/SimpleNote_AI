package com.simplenote.backend.service;

import com.simplenote.backend.pojo.FollowStateVO;
import com.simplenote.backend.pojo.PageBean;
import com.simplenote.backend.pojo.UserDetailVO;

public interface FollowService {
    // 显式设置关注状态
    FollowStateVO setFollowState(Integer followedId, boolean desiredFollowing);
    // 检查关注状态
    boolean isFollowing(Integer followerId, Integer followedId);
    // 支持分页
    PageBean<UserDetailVO> getFollowingList(Integer userId, Integer pageNum, Integer pageSize);
    PageBean<UserDetailVO> getFollowersList(Integer userId, Integer pageNum, Integer pageSize);
}
