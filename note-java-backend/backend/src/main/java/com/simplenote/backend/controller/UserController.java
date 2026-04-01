package com.simplenote.backend.controller;

import com.simplenote.backend.pojo.Result;
import com.simplenote.backend.pojo.User;
import com.simplenote.backend.service.UserService;
import com.simplenote.backend.utils.JwtUtils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result<?> register(String username, String password) {
        // 1. 查询用户名是否被占用
        User u = userService.findByUsername(username);
        if (u == null) {
            // 2. 没被占用，开始注册
            userService.register(username, password);
            return Result.success();
        } else {
            // 3. 被占用了，报个错
            return Result.error("用户名已被占用，请换一个吧！");
        }
    }

    @PostMapping("/login")
    public Result<String> login(String username, String password) {
        // 1. 验证账号密码
        User user = userService.login(username, password);
        
        if (user != null) {
            // 2. 验证成功，把用户ID和用户名装进 Token 里
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", user.getId());
            claims.put("username", user.getUsername());
            
            String token = JwtUtils.createToken(claims);
            // 3. 把这张“证”发给前端
            return Result.success(token);
        }
        return Result.error("账号或密码错误");
    }
}