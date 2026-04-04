package com.simplenote.backend.service.impl;

import com.simplenote.backend.mapper.FollowMapper;
import com.simplenote.backend.mapper.UserMapper;
import com.simplenote.backend.pojo.UserDetailVO;
import com.simplenote.backend.service.FollowService;
import com.simplenote.backend.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class FollowServiceImpl implements FollowService {

    @Autowired
    private FollowMapper followMapper;
    @Autowired
    private UserMapper userMapper;
    @Override
    public String toggleFollow(Integer followedId) {
        // 1. 获取当前登录用户的 ID
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer myId = (Integer) map.get("id");

        // 2. 防御性判断：不能自己关注自己
        if (myId.equals(followedId)) {
            throw new RuntimeException("不能关注你自己哦");
        }

        // 3. 核心业务逻辑
        if (followMapper.isFollowing(myId, followedId) > 0) {
            followMapper.unfollow(myId, followedId);
            return "已取消关注";
        } else {
            followMapper.follow(myId, followedId);
            return "关注成功";
        }
    }

    @Override
    public List<UserDetailVO> getFollowingList(Integer userId) {
        return userMapper.getFollowingList(userId);
    }

    @Override
    public List<UserDetailVO> getFollowersList(Integer userId) {
        return userMapper.getFollowersList(userId);
    }
}