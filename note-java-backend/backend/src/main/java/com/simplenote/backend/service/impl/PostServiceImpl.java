package com.simplenote.backend.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simplenote.backend.mapper.PostMapper;
import com.simplenote.backend.mapper.UserLikesMapper;
import com.simplenote.backend.pojo.LikeStateVO;
import com.simplenote.backend.pojo.PageBean;
import com.simplenote.backend.pojo.Post;
import com.simplenote.backend.pojo.PostVO;
import com.simplenote.backend.service.PostService;
import com.simplenote.backend.interaction.InteractionRedisService;
import com.simplenote.backend.security.context.UserContextHolder;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostServiceImpl implements PostService {
    private static final String POST_DETAIL_CACHE_KEY = "post:detail:";
    private static final String NULL_CACHE_VALUE = "NULL";

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserLikesMapper userLikesMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private InteractionRedisService interactionRedisService;

    @Value("${cache.post.detail-ttl}")
    private Duration postDetailTtl;

    @Value("${cache.post.null-ttl}")
    private Duration postNullTtl;

    // 统一处理点赞状态的方法
    private void populateLikeStatus(List<PostVO> posts) {
        if (posts == null || posts.isEmpty()) {
            return;
        }
        // 1. 尝试获取当前登录用户
        Map<String, Object> map = UserContextHolder.get();
        if (map == null || map.get("id") == null) {
            // 用户未登录，所有帖子都标为未点赞
            posts.forEach(post -> post.setIsLiked(false));
            return;
        }
        Integer currentUserId = (Integer) map.get("id");
        // 2. 提取当前页所有帖子的 ID
        List<Integer> postIds = posts.stream()
                .map(PostVO::getId)
                .collect(Collectors.toList());
        if (postIds.isEmpty()) return;
        // 3. 一次性从数据库中查出当前用户点赞过这些 ID 中的哪几个
        List<Integer> likedPostIds = userLikesMapper.checkUserLikesInBatch(currentUserId, postIds);
        // 4. 给 VO 赋值
        posts.forEach(post -> {
            post.setIsLiked(likedPostIds.contains(post.getId()));
        });
    }

    @Override
    public void add(Post post) {
        postMapper.add(post);
    }

    @Override
    public List<PostVO> listWithAuthor() {
        return postMapper.listWithAuthor();
    }

    @Override
    public PageBean<PostVO> listWithPage(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<PostVO> list = postMapper.listWithAuthor();
        PageInfo<PostVO> pageInfo = new PageInfo<>(list);
        
        populateLikeStatus(pageInfo.getList());
        applyCachedLikeMetrics(pageInfo.getList());
        
        return new PageBean<>(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public PageBean<PostVO> pageQueryByUser(Integer userId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<PostVO> list = postMapper.listByUserId(userId); 
        PageInfo<PostVO> pageInfo = new PageInfo<>(list);
        
        populateLikeStatus(pageInfo.getList());
        applyCachedLikeMetrics(pageInfo.getList());
        
        return new PageBean<>(pageInfo.getTotal(), pageInfo.getList());
    }

    @Transactional  
    @Override
    public LikeStateVO setLikeState(Integer postId, boolean desiredLiked) {
        Post post = postMapper.findById(postId);
        if (post == null) {
            throw new RuntimeException("点赞失败：该帖子已被删除或不存在！");
        }
        
        Map<String, Object> map = UserContextHolder.get();
        Integer userId = (Integer) map.get("id");
        LikeStateVO state = interactionRedisService.setPostLikeState(postId, userId, desiredLiked);

        evictPostDetailCache(postId);
        return state;
    }

    @Override
    public PageBean<PostVO> listLiked(Integer userId, Integer pageNum, Integer pageSize) {
        long total = interactionRedisService.getLikedPostCount(userId);
        List<Integer> postIds = interactionRedisService.getLikedPostIdsPage(userId, pageNum, pageSize);
        if (postIds.isEmpty()) {
            return new PageBean<>(total, Collections.emptyList());
        }

        List<PostVO> list = postMapper.listByIds(postIds);
        sortPostsByIdOrder(list, postIds);
        populateLikeStatus(list);
        applyCachedLikeMetrics(list);

        return new PageBean<>(total, list);
    }

    @Override
    public int softDelete(Integer id, Integer userId) {
        int row = postMapper.softDelete(id, userId);
        if (row > 0) {
            evictPostDetailCache(id);
        }
        return row;
    }

    @Override
    public PostVO getPostDetailById(Integer id) {
        String cacheKey = Objects.requireNonNull(buildPostDetailCacheKey(id));
        String cachedValue = stringRedisTemplate.opsForValue().get(cacheKey);

        if (NULL_CACHE_VALUE.equals(cachedValue)) {
            return null;
        }

        if (cachedValue != null && !cachedValue.isBlank()) {
            PostVO cachedPost = readPostFromCache(cachedValue);
            populateLikeStatus(Collections.singletonList(cachedPost));
            applyCachedLikeMetrics(Collections.singletonList(cachedPost));
            return cachedPost;
        }

        PostVO postVO = postMapper.getPostDetailById(id);
        if (postVO == null) {
            stringRedisTemplate.opsForValue().set(
                    cacheKey,
                    NULL_CACHE_VALUE,
                    Objects.requireNonNull(postNullTtl)
            );
            return null;
        }

        populateLikeStatus(Collections.singletonList(postVO));
        applyCachedLikeMetrics(Collections.singletonList(postVO));
        writePostToCache(cacheKey, postVO);
        return postVO;
    }

    private void applyCachedLikeMetrics(List<PostVO> posts) {
        if (posts == null || posts.isEmpty()) {
            return;
        }

        Map<String, Object> map = UserContextHolder.get();
        Integer currentUserId = map == null ? null : (Integer) map.get("id");

        posts.forEach(post -> {
            Integer cachedLikeCount = interactionRedisService.getCachedLikeCount(post.getId());
            if (cachedLikeCount != null) {
                post.setLikeCount(cachedLikeCount);
                post.setLikesCount(cachedLikeCount);
            }

            if (currentUserId == null) {
                return;
            }

            Boolean cachedLikeStatus = interactionRedisService.getCachedLikeStatus(post.getId(), currentUserId);
            if (cachedLikeStatus != null) {
                post.setIsLiked(cachedLikeStatus);
            }
        });
    }

    private void sortPostsByIdOrder(List<PostVO> posts, List<Integer> orderedIds) {
        Map<Integer, Integer> orderMap = new HashMap<>(orderedIds.size());
        for (int i = 0; i < orderedIds.size(); i++) {
            orderMap.put(orderedIds.get(i), i);
        }

        posts.sort(Comparator.comparingInt(post -> orderMap.getOrDefault(post.getId(), Integer.MAX_VALUE)));
    }

    @NonNull
    private String buildPostDetailCacheKey(Integer postId) {
        return POST_DETAIL_CACHE_KEY + postId;
    }

    private void evictPostDetailCache(Integer postId) {
        stringRedisTemplate.delete(buildPostDetailCacheKey(postId));
    }

    private void writePostToCache(@NonNull String cacheKey, PostVO postVO) {
        try {
            String payload = Objects.requireNonNull(objectMapper.writeValueAsString(postVO));
            stringRedisTemplate.opsForValue().set(
                    cacheKey,
                    payload,
                    Objects.requireNonNull(postDetailTtl)
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize post cache.", e);
        }
    }

    private PostVO readPostFromCache(String cachedValue) {
        try {
            return objectMapper.readValue(cachedValue, PostVO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize post cache.", e);
        }
    }
}
