package com.simplenote.backend.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.simplenote.backend.mapper.PostMapper;
import com.simplenote.backend.mapper.UserLikesMapper;
import com.simplenote.backend.pojo.PageBean;
import com.simplenote.backend.pojo.Post;
import com.simplenote.backend.pojo.PostVO;
import com.simplenote.backend.service.PostService;
import com.simplenote.backend.utils.ThreadLocalUtil;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostServiceImpl implements PostService {
    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserLikesMapper userLikesMapper;

    // 统一处理点赞状态的方法
    private void populateLikeStatus(List<PostVO> posts) {
        if (posts == null || posts.isEmpty()) {
            return;
        }
        // 1. 尝试获取当前登录用户
        Map<String, Object> map = ThreadLocalUtil.get();
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
        
        return new PageBean<>(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public PageBean<PostVO> pageQueryByUser(Integer userId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<PostVO> list = postMapper.listByUserId(userId); 
        PageInfo<PostVO> pageInfo = new PageInfo<>(list);
        
        populateLikeStatus(pageInfo.getList());
        
        return new PageBean<>(pageInfo.getTotal(), pageInfo.getList());
    }

    @Transactional  
    @Override
    public void toggleLike(Integer postId) {
        Post post = postMapper.findById(postId);
        if (post == null) {
            throw new RuntimeException("点赞失败：该帖子已被删除或不存在！");
        }
        
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");
        int count = userLikesMapper.checkUserLike(userId, postId);

        if (count > 0) {
            userLikesMapper.removeLike(userId, postId);
            postMapper.decrementLikes(postId);
        } else {
            userLikesMapper.addLike(userId, postId);
            postMapper.incrementLikes(postId);
        }
    }

    @Override
    public PageBean<PostVO> listLiked(Integer userId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<PostVO> list = postMapper.listLiked(userId);
        PageInfo<PostVO> pageInfo = new PageInfo<>(list);
        
        populateLikeStatus(pageInfo.getList());
        
        return new PageBean<>(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public int softDelete(Integer id, Integer userId) {
        return postMapper.softDelete(id, userId);
    }

    @Override
    public PostVO getPostDetailById(Integer id) {
        PostVO postVO = postMapper.getPostDetailById(id);
        if (postVO != null) {
            populateLikeStatus(Collections.singletonList(postVO));
        }
        return postVO;
    }
}