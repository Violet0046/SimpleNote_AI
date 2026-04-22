package com.simplenote.backend.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.simplenote.backend.mapper.UserMapper;
import com.simplenote.backend.pojo.FollowStateVO;
import com.simplenote.backend.pojo.PageBean;
import com.simplenote.backend.pojo.UserDetailVO;
import com.simplenote.backend.service.FollowService;
import com.simplenote.backend.interaction.InteractionRedisService;
import com.simplenote.backend.security.context.UserContextHolder;
import com.simplenote.backend.security.jwt.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Map;

public class FollowRedisServiceImpl implements FollowService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private InteractionRedisService interactionRedisService;

    @Override
    public FollowStateVO setFollowState(Integer followedId, boolean desiredFollowing) {
        Integer myId = getCurrentUserId();
        if (myId == 0) {
            throw new RuntimeException("请先登录");
        }
        if (myId.equals(followedId)) {
            throw new RuntimeException("不能关注自己");
        }

        return interactionRedisService.setFollowState(myId, followedId, desiredFollowing);
    }

    @Override
    public boolean isFollowing(Integer followerId, Integer followedId) {
        return interactionRedisService.getFollowStatus(followerId, followedId);
    }

    @Override
    public PageBean<UserDetailVO> getFollowingList(Integer userId, Integer pageNum, Integer pageSize) {
        Integer myId = getCurrentUserId();
        PageHelper.startPage(pageNum, pageSize);
        List<UserDetailVO> list = userMapper.getFollowingList(userId, myId);
        applyFollowStatusFromCache(list, myId);
        PageInfo<UserDetailVO> pageInfo = new PageInfo<>(list);
        return new PageBean<>(pageInfo.getTotal(), list);
    }

    @Override
    public PageBean<UserDetailVO> getFollowersList(Integer userId, Integer pageNum, Integer pageSize) {
        Integer myId = getCurrentUserId();
        PageHelper.startPage(pageNum, pageSize);
        List<UserDetailVO> list = userMapper.getFollowersList(userId, myId);
        applyFollowStatusFromCache(list, myId);
        PageInfo<UserDetailVO> pageInfo = new PageInfo<>(list);
        return new PageBean<>(pageInfo.getTotal(), list);
    }

    private void applyFollowStatusFromCache(List<UserDetailVO> list, Integer myId) {
        if (list == null || list.isEmpty() || myId == null || myId == 0) {
            return;
        }

        list.forEach(user -> {
            user.setIsFollowing(interactionRedisService.getFollowStatus(myId, user.getId()) ? 1 : 0);
            user.setIsFollower(interactionRedisService.getFollowStatus(user.getId(), myId) ? 1 : 0);
        });
    }

    private Integer getCurrentUserId() {
        try {
            Map<String, Object> map = UserContextHolder.get();
            if (map != null && map.get("id") != null) {
                return (Integer) map.get("id");
            }

            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String token = request.getHeader("Authorization");
                if (token != null && !token.isEmpty()) {
                    Map<String, Object> claims = JwtUtils.parseToken(token);
                    if (claims != null && claims.get("id") != null) {
                        return (Integer) claims.get("id");
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return 0;
    }
}
