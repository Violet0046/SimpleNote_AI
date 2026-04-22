package com.simplenote.backend.service.impl;

import com.simplenote.backend.mapper.PostMapper;
import com.simplenote.backend.mapper.UserMapper;
import com.simplenote.backend.pojo.Post;
import com.simplenote.backend.pojo.User;
import com.simplenote.backend.pojo.UserDetailVO;
import com.simplenote.backend.service.UserService;
import com.simplenote.backend.interaction.InteractionRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private InteractionRedisService interactionRedisService;

    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    public void register(String username, String password, String nickname, Integer gender, String avatarUrl) {
        // 可以在这里给密码加密（MD5或BCrypt），现存明文
        userMapper.add(username, password, nickname, gender, avatarUrl);
    }

    @Override
    public User login(String username, String password) {
        // 未实现密码比对（比如 BCrypt 解密）
        // 为了快速跑通，先直接去数据库里查账号密码
        return userMapper.findByUsernameAndPassword(username, password);
    }

    @Override
    public UserDetailVO getUserDetailById(Integer id) {
        UserDetailVO userDetailVO = userMapper.getUserDetailById(id);
        
        // 容错处理：万一查出来的统计数据是 null，给个默认值 0 (防御性编程)
        if (userDetailVO != null) {
            userDetailVO.setFollowingCount(Math.toIntExact(interactionRedisService.getFollowingCount(id)));
            userDetailVO.setFollowersCount(Math.toIntExact(interactionRedisService.getFollowersCount(id)));
            userDetailVO.setLikesCount(calculateRealtimeReceivedLikes(id));
        }
        
        return userDetailVO;
    }
    
    @Override
    public void updateInfo(User user) {
        userMapper.update(user);
    }

    private Integer calculateRealtimeReceivedLikes(Integer userId) {
        List<Post> posts = postMapper.findLikeStatsByUserId(userId);
        int totalLikes = 0;
        for (Post post : posts) {
            Integer cachedLikeCount = interactionRedisService.getCachedLikeCount(post.getId());
            if (cachedLikeCount != null) {
                totalLikes += cachedLikeCount;
            } else if (post.getLikesCount() != null) {
                totalLikes += post.getLikesCount();
            }
        }
        return totalLikes;
    }
}
