package com.simplenote.backend.controller;

import com.simplenote.backend.mapper.FollowMapper;
import com.simplenote.backend.pojo.Result;
import com.simplenote.backend.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/follow")
public class FollowController {

    @Autowired
    private FollowMapper followMapper;

    @PostMapping("/{followedId}")
    public Result<String> toggleFollow(@PathVariable Integer followedId) {
        // 1. 获取当前登录用户的 ID
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer myId = (Integer) map.get("id");

        // 2. 防御性判断：不能自己关注自己
        if (myId.equals(followedId)) {
            return Result.error("不能关注你自己哦");
        }

        // 3. 业务逻辑判断：如果已经关注了，就取关；如果没有关注，就关注
        if (followMapper.isFollowing(myId, followedId) > 0) {
            followMapper.unfollow(myId, followedId);
            return Result.success("已取消关注");
        } else {
            followMapper.follow(myId, followedId);
            return Result.success("关注成功");
        }
    }
}