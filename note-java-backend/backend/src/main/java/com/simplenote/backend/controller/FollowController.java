package com.simplenote.backend.controller;

import com.simplenote.backend.pojo.Result;
import com.simplenote.backend.pojo.UserDetailVO;
import com.simplenote.backend.service.FollowService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/follow")
public class FollowController {

    @Autowired
    private FollowService followService;

    @PostMapping("/{followedId}")
    public Result<String> toggleFollow(@PathVariable Integer followedId) {
        try {
            String msg = followService.toggleFollow(followedId);
            return Result.success(msg);
        } catch (Exception e) {
            // 捕获 "不能关注你自己哦" 等异常并返回给前端
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/following/{userId}")
    public Result<List<UserDetailVO>> getFollowing(@PathVariable Integer userId) {
        return Result.success(followService.getFollowingList(userId));
    }

    @GetMapping("/followers/{userId}")
    public Result<List<UserDetailVO>> getFollowers(@PathVariable Integer userId) {
        return Result.success(followService.getFollowersList(userId));
    }
}