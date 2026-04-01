package com.simplenote.backend.service.impl;

import com.simplenote.backend.mapper.UserMapper;
import com.simplenote.backend.pojo.User;
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
    public void register(String username, String password) {
        // 可以在这里给密码加密（MD5或BCrypt），现存明文
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setNickname("新用户_" + username); // 默认起个昵称
        userMapper.add(user);
    }

    @Override
    public User login(String username, String password) {
    // 未实现密码比对（比如 BCrypt 解密）
    // 为了快速跑通，先直接去数据库里查账号密码
    return userMapper.findByUsernameAndPassword(username, password);
    }
}