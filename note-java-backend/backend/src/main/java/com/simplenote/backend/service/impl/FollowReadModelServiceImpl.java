package com.simplenote.backend.service.impl;

import com.simplenote.backend.mapper.UserMapper;
import com.simplenote.backend.pojo.PageBean;
import com.simplenote.backend.pojo.UserDetailVO;
import com.simplenote.backend.service.FollowService;
import com.simplenote.backend.service.support.InteractionRedisService;
import com.simplenote.backend.utils.JwtUtils;
import com.simplenote.backend.utils.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Primary
@Service
public class FollowReadModelServiceImpl implements FollowService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private InteractionRedisService interactionRedisService;

    @Override
    public String toggleFollow(Integer followedId) {
        Integer myId = getCurrentUserId();
        if (myId == 0) {
            throw new RuntimeException("请先登录");
        }
        if (myId.equals(followedId)) {
            throw new RuntimeException("不能关注自己");
        }

        return interactionRedisService.toggleFollow(myId, followedId) ? "关注成功" : "已取消关注";
    }

    @Override
    public boolean isFollowing(Integer followerId, Integer followedId) {
        return interactionRedisService.getFollowStatus(followerId, followedId);
    }

    @Override
    public PageBean<UserDetailVO> getFollowingList(Integer userId, Integer pageNum, Integer pageSize) {
        long total = interactionRedisService.getFollowingCount(userId);
        List<Integer> userIds = interactionRedisService.getFollowingIdsPage(userId, pageNum, pageSize);
        return buildRelationPage(userIds, total, getCurrentUserId());
    }

    @Override
    public PageBean<UserDetailVO> getFollowersList(Integer userId, Integer pageNum, Integer pageSize) {
        long total = interactionRedisService.getFollowersCount(userId);
        List<Integer> userIds = interactionRedisService.getFollowerIdsPage(userId, pageNum, pageSize);
        return buildRelationPage(userIds, total, getCurrentUserId());
    }

    private PageBean<UserDetailVO> buildRelationPage(List<Integer> userIds, long total, Integer myId) {
        if (userIds.isEmpty()) {
            return new PageBean<>(total, Collections.emptyList());
        }

        List<UserDetailVO> users = userMapper.findUserCardsByIds(userIds);
        sortUsersByIdOrder(users, userIds);
        applyFollowStatus(users, myId);
        return new PageBean<>(total, users);
    }

    private void sortUsersByIdOrder(List<UserDetailVO> users, List<Integer> orderedIds) {
        Map<Integer, Integer> orderMap = new HashMap<>(orderedIds.size());
        for (int i = 0; i < orderedIds.size(); i++) {
            orderMap.put(orderedIds.get(i), i);
        }
        users.sort(Comparator.comparingInt(user -> orderMap.getOrDefault(user.getId(), Integer.MAX_VALUE)));
    }

    private void applyFollowStatus(List<UserDetailVO> users, Integer myId) {
        users.forEach(user -> {
            if (myId == null || myId == 0) {
                user.setIsFollowing(0);
                user.setIsFollower(0);
                return;
            }

            user.setIsFollowing(interactionRedisService.getFollowStatus(myId, user.getId()) ? 1 : 0);
            user.setIsFollower(interactionRedisService.getFollowStatus(user.getId(), myId) ? 1 : 0);
        });
    }

    private Integer getCurrentUserId() {
        try {
            Map<String, Object> map = ThreadLocalUtil.get();
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
