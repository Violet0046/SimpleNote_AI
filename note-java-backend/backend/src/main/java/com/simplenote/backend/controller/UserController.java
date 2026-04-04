package com.simplenote.backend.controller;

import com.simplenote.backend.pojo.Result;
import com.simplenote.backend.pojo.User;
import com.simplenote.backend.pojo.UserDetailVO;
import com.simplenote.backend.service.UserService;
import com.simplenote.backend.utils.JwtUtils;
import com.simplenote.backend.utils.ThreadLocalUtil;

import jakarta.validation.constraints.Pattern;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    // 注册接口：接收账号、密码、性别，昵称和头像URL
    @PostMapping("/register")
    public Result<String> register(
            @Pattern(regexp = "^\\S{1,20}$") String username, 
            @Pattern(regexp = "^\\S{4,20}$") String password,
            @RequestParam(defaultValue = "0") Integer gender,
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) String avatarUrl) {
        
        // 1. 检查账号是否被注册
        User u = userService.findByUsername(username);
        
        if (u == null) {
            // 2. 处理头像：如果前端传了头像就用前端的；如果没传，就随机给个默认的兜底
            String finalAvatarUrl;
            if (avatarUrl != null && !avatarUrl.trim().isEmpty()) {
                finalAvatarUrl = avatarUrl;
            } else {
                int randomNum = new Random().nextInt(7) + 1; 
                finalAvatarUrl = "http://localhost:8080/" + randomNum + ".jpg";
            }
            
            // 3. 处理昵称：如果前端传了昵称就用前端的；如果没传，随机兜底
            String finalNickname;
            if (nickname != null && !nickname.trim().isEmpty()) {
                finalNickname = nickname;
            } else {
                finalNickname = "小红薯_" + (new Random().nextInt(9000) + 1000);
            }

            // 4. 调用 Service 层进行注册
            userService.register(username, password, finalNickname, gender, finalAvatarUrl);
            
            return Result.success("注册成功！");
        } else {
            return Result.error("用户已注册");
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

    // 获取当前登录用户的个人主页详细信息
    @GetMapping("/me")
    public Result<UserDetailVO> userDetailInfo() {
        // 1. 从保安 (ThreadLocal) 那里拿到当前登录的人是谁
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");

        // 2. 去 Service 层拿聚合好的数据
        UserDetailVO detailVO = userService.getUserDetailById(userId);

        return Result.success(detailVO);
    }

    // 获取他人主页详细信息
    @GetMapping("/info/{id}")
    public Result<UserDetailVO> getUserInfoById(@PathVariable Integer id) {
        UserDetailVO userDetailVO = userService.getUserDetailById(id);
        return Result.success(userDetailVO);
    }

    @PostMapping("/update")
    public Result<String> update(@RequestBody User user) {
        // 1. 获取当前登录用户 ID，防止用户恶意修改别人的 ID
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer myId = (Integer) map.get("id");
        user.setId(myId);

        // 2. 更新资料
        userService.updateInfo(user);
        return Result.success("资料更新成功！");
    }
}