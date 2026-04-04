package com.simplenote.backend.service;

import com.simplenote.backend.pojo.PageBean;
import com.simplenote.backend.pojo.UserDetailVO;

public interface FollowService {
    // 切换关注状态，返回提示信息
    String toggleFollow(Integer followedId);
    // 检查关注状态
    boolean isFollowing(Integer followerId, Integer followedId);
    // 支持分页
    PageBean<UserDetailVO> getFollowingList(Integer userId, Integer pageNum, Integer pageSize);
    PageBean<UserDetailVO> getFollowersList(Integer userId, Integer pageNum, Integer pageSize);
}