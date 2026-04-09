package com.simplenote.backend.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.simplenote.backend.mapper.FollowMapper;
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

import java.util.List;
import java.util.Map;

@Primary
@Service
public class FollowRedisServiceImpl implements FollowService {

    @Autowired
    private FollowMapper followMapper;

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
        Boolean cachedStatus = interactionRedisService.getCachedFollowStatus(followerId, followedId);
        if (cachedStatus != null) {
            return cachedStatus;
        }

        Integer count = followMapper.checkFollowStatus(followerId, followedId);
        return count != null && count > 0;
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
            Boolean cachedFollowing = interactionRedisService.getCachedFollowStatus(myId, user.getId());
            if (cachedFollowing != null) {
                user.setIsFollowing(cachedFollowing ? 1 : 0);
            }

            Boolean cachedFollower = interactionRedisService.getCachedFollowStatus(user.getId(), myId);
            if (cachedFollower != null) {
                user.setIsFollower(cachedFollower ? 1 : 0);
            }
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
