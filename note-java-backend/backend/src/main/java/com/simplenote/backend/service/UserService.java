package com.simplenote.backend.service;
import com.simplenote.backend.pojo.User;
import com.simplenote.backend.pojo.UserDetailVO;

public interface UserService {
    // 根据用户名查询用户
    User findByUsername(String username);
    // 用户注册
    void register(String username, String password, String nickname, String avatarUrl);
    // 用户登录
    User login(String username, String password);

    // 获取用户主页详细信息
    UserDetailVO getUserDetailInfo(Integer userId);
}