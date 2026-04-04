package com.simplenote.backend.service;

public interface FollowService {
    // 切换关注状态，返回提示信息
    String toggleFollow(Integer followedId);
}