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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result<String> register(@Pattern(regexp = "^\\S{1,20}$") String username, 
                                   @Pattern(regexp = "^\\S{4,20}$") String password) {
        // 1. 检查账号是否被注册
        User u = userService.findByUsername(username);
        
        if (u == null) {
            // 2. 账号没被占用，准备注册！
            
            // 随机分配 1~5 号头像 (假设你在 static 文件夹放了 5 张图)
            int randomNum = new java.util.Random().nextInt(7) + 1; 
            String randomAvatarUrl = "http://localhost:8080/" + randomNum + ".png";
            
            // 生成默认昵称 (比如：小红薯_1357)
            String randomNickname = "小红薯_" + (new java.util.Random().nextInt(9000) + 1000);

            // 3. 调用 Service 层进行注册 (注意！这里你需要修改一下 userService.register 的参数，把这两个新字段传进去)
            userService.register(username, password, randomNickname, randomAvatarUrl);
            
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
    @GetMapping("/info/detail")
    public Result<UserDetailVO> userDetailInfo() {
        // 1. 从保安 (ThreadLocal) 那里拿到当前登录的人是谁
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");

        // 2. 去 Service 层拿聚合好的数据
        UserDetailVO detailVO = userService.getUserDetailInfo(userId);

        // 3. 完美返回给前端！
        return Result.success(detailVO);
    }

    @GetMapping("/info/{id}")
    public Result<UserDetailVO> getUserInfoById(@PathVariable Integer id) {
        UserDetailVO userDetailVO = userService.getUserDetailById(id);
        return Result.success(userDetailVO);
    }
}