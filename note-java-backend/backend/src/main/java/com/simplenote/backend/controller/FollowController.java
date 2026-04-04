package com.simplenote.backend.controller;

import com.simplenote.backend.pojo.PageBean;
import com.simplenote.backend.pojo.Result;
import com.simplenote.backend.pojo.UserDetailVO;
import com.simplenote.backend.service.FollowService;
import com.simplenote.backend.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/follow")
public class FollowController {

    @Autowired
    private FollowService followService;

    @PostMapping("/{followedId}")
    public Result<String> toggleFollow(@PathVariable("followedId") Integer followedId) {
        try {
            String msg = followService.toggleFollow(followedId);
            return Result.success(msg);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    // 核心修复：显式声明 ("userId"), ("pageNum"), ("pageSize")
    @GetMapping("/following/{userId}")
    public Result<PageBean<UserDetailVO>> getFollowing(
            @PathVariable("userId") Integer userId,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize) {
        return Result.success(followService.getFollowingList(userId, pageNum, pageSize));
    }

    @GetMapping("/followers/{userId}")
    public Result<PageBean<UserDetailVO>> getFollowers(
            @PathVariable("userId") Integer userId,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize) {
        return Result.success(followService.getFollowersList(userId, pageNum, pageSize));
    }

    @GetMapping("/status/{id}")
    public Result<Boolean> getFollowStatus(@PathVariable("id") Integer targetId) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer myId = (Integer) map.get("id");
        boolean isFollowing = followService.isFollowing(myId, targetId);
        return Result.success(isFollowing);
    }
}