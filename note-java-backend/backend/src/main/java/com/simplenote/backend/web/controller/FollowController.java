package com.simplenote.backend.web.controller;

import com.simplenote.backend.pojo.FollowStateVO;
import com.simplenote.backend.pojo.PageBean;
import com.simplenote.backend.pojo.Result;
import com.simplenote.backend.pojo.UserDetailVO;
import com.simplenote.backend.security.jwt.JwtUtils;
import com.simplenote.backend.service.FollowService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/follow")
public class FollowController {

    @Autowired
    private FollowService followService;

    @PutMapping("/{followedId}")
    public Result<FollowStateVO> follow(@PathVariable("followedId") Integer followedId) {
        try {
            return Result.success(followService.setFollowState(followedId, true));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{followedId}")
    public Result<FollowStateVO> unfollow(@PathVariable("followedId") Integer followedId) {
        try {
            return Result.success(followService.setFollowState(followedId, false));
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
    public Result<Boolean> getFollowStatus(@PathVariable("id") Integer targetId, HttpServletRequest request) {
        try {
            // 自己动手丰衣足食，直接从 Request Header 里抠出 Authorization
            String token = request.getHeader("Authorization");
            if (token != null && !token.isEmpty()) {
                Map<String, Object> claims = JwtUtils.parseToken(token);
                Integer myId = (Integer) claims.get("id");
                
                // 去数据库查状态
                boolean isFollowing = followService.isFollowing(myId, targetId);
                return Result.success(isFollowing);
            }
        } catch (Exception e) {
            // 如果没登录，或者 Token 过期，一律当做没关注
        }
        return Result.success(false);
    }
}
