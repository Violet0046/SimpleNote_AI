package com.simplenote.backend.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.simplenote.backend.mapper.FollowMapper;
import com.simplenote.backend.mapper.UserMapper;
import com.simplenote.backend.pojo.PageBean;
import com.simplenote.backend.pojo.UserDetailVO;
import com.simplenote.backend.service.FollowService;
import com.simplenote.backend.utils.JwtUtils;
import com.simplenote.backend.utils.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Map;

@Service
public class FollowServiceImpl implements FollowService {

    @Autowired
    private FollowMapper followMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public String toggleFollow(Integer followedId) {
        Integer myId = getCurrentUserId();
        if (myId == 0) {
            throw new RuntimeException("请先登录");
        }
        if (myId.equals(followedId)) {
            throw new RuntimeException("不能关注你自己哦");
        }

        if (followMapper.isFollowing(myId, followedId) > 0) {
            followMapper.unfollow(myId, followedId);
            return "已取消关注";
        } else {
            followMapper.follow(myId, followedId);
            return "关注成功";
        }
    }

    @Override
    public PageBean<UserDetailVO> getFollowingList(Integer userId, Integer pageNum, Integer pageSize) {
        Integer myId = getCurrentUserId();
        PageHelper.startPage(pageNum, pageSize);
        List<UserDetailVO> list = userMapper.getFollowingList(userId, myId);
        PageInfo<UserDetailVO> pageInfo = new PageInfo<>(list);
        return new PageBean<>(pageInfo.getTotal(), list);
    }

    @Override
    public PageBean<UserDetailVO> getFollowersList(Integer userId, Integer pageNum, Integer pageSize) {
        Integer myId = getCurrentUserId();
        PageHelper.startPage(pageNum, pageSize);
        List<UserDetailVO> list = userMapper.getFollowersList(userId, myId);
        PageInfo<UserDetailVO> pageInfo = new PageInfo<>(list);
        return new PageBean<>(pageInfo.getTotal(), list);
    }

    @Override
    public boolean isFollowing(Integer followerId, Integer followedId) {
        Integer count = followMapper.checkFollowStatus(followerId, followedId);
        return count != null && count > 0;
    }

    // 修复：穿透拦截器白名单，手动解析 HTTP 请求头里的 Token 拿真实用户 ID
    private Integer getCurrentUserId() {
        try {
            // 1. 常规获取（针对没有被白名单放行的接口）
            Map<String, Object> map = ThreadLocalUtil.get();
            if (map != null && map.get("id") != null) {
                return (Integer) map.get("id");
            }
            
            // 2. 暴力破解：如果拦截器罢工了，我们自己从请求头里把 Token 抠出来解析！
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
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
        } catch (Exception e) {}
        return 0; // 真正的没登录游客才返回 0
    }
}