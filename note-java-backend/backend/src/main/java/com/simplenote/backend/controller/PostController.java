package com.simplenote.backend.controller;

import com.simplenote.backend.pojo.Post;
import com.simplenote.backend.pojo.Result;
import com.simplenote.backend.service.PostService;
import com.simplenote.backend.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping("/add")
    // @RequestBody 的作用：告诉服务器，前端发来的是 application/json 格式的数据，请自动帮我塞进 Post 对象里！
    public Result<String> add(@RequestBody Post post) {
        
        // 1. 验明正身：从ThreadLocal拿到登录用户信息
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");

        // 2. 强行覆盖：不管前端传没传 user_id，我们都强制把它设置成真正的登录用户 ID
        post.setUserId(userId);

        // 3. 存数据库
        postService.add(post);

        return Result.success("帖子发布成功！");
    }
}