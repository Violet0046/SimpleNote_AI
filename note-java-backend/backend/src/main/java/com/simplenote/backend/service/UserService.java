package com.simplenote.backend.service;
import com.simplenote.backend.pojo.User;
import com.simplenote.backend.pojo.UserDetailVO;

public interface UserService {
    // 根据用户名查询用户
    User findByUsername(String username);
    // 用户注册
    void register(String username, String password, String nickname, Integer gender, String avatarUrl);
    // 用户登录
    User login(String username, String password);

    // 根据用户ID获取用户信息
    UserDetailVO getUserDetailById(Integer id);
    // 修改个人资料
    void updateInfo(User user);
}