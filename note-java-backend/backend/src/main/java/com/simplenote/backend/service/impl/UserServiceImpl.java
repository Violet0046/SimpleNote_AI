package com.simplenote.backend.service.impl;

import com.simplenote.backend.mapper.UserMapper;
import com.simplenote.backend.pojo.User;
import com.simplenote.backend.pojo.UserDetailVO;
import com.simplenote.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    public void register(String username, String password, String nickname, String avatarUrl) {
        // 可以在这里给密码加密（MD5或BCrypt），现存明文
        userMapper.add(username, password, nickname, avatarUrl);
    }

    @Override
    public User login(String username, String password) {
    // 未实现密码比对（比如 BCrypt 解密）
    // 为了快速跑通，先直接去数据库里查账号密码
    return userMapper.findByUsernameAndPassword(username, password);
    }

    @Override
    public UserDetailVO getUserDetailInfo(Integer userId) {
        // 直接调用 Mapper 拿到一站式聚合好的数据
        UserDetailVO userDetailVO = userMapper.getUserDetailById(userId);
        
        // 容错处理：万一查出来的统计数据是 null，给个默认值 0 (防御性编程)
        if (userDetailVO != null) {
            if (userDetailVO.getFollowingCount() == null) userDetailVO.setFollowingCount(0);
            if (userDetailVO.getFollowersCount() == null) userDetailVO.setFollowersCount(0);
            if (userDetailVO.getLikesCount() == null) userDetailVO.setLikesCount(0);
        }
        
        return userDetailVO;
    }
}