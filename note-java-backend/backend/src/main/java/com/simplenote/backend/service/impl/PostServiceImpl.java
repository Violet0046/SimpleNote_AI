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

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostServiceImpl implements PostService {
    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserLikesMapper userLikesMapper;

    // 私有统一处理方法，专门给各种列表做数据瘦身！
    private void processPostList(List<PostVO> list) {
        for (PostVO vo : list) {
            // 1. 处理封面图：如果有逗号，只截取第一张图片作为封面
            String images = vo.getImages();
            if (images != null && images.contains(",")) {
                vo.setImages(images.split(",")[0]); 
            }
            // 2. 清空长文本内容，节省网络带宽 (反正列表页只看图和标题)
            vo.setContent(null); 
        }
    }

    @Override
    public void add(Post post) {
        postMapper.add(post);
    }

    @Override
    public List<PostVO> listWithAuthor() {
        List<PostVO> list = postMapper.listWithAuthor();
        processPostList(list); // 调用瘦身逻辑
        return list; // 返回处理过的数据，而不是再去查一遍数据库！
    }

    @Override
    public PageBean<PostVO> listWithPage(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<PostVO> list = postMapper.listWithAuthor();
        
        processPostList(list); // 让分页列表也享受瘦身优化！

        PageInfo<PostVO> pageInfo = new PageInfo<>(list);
        return new PageBean<>(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public PageBean<PostVO> pageQueryByUser(Integer userId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        // 统一调用合并后的接口！
        List<PostVO> list = postMapper.listByUserId(userId); 
        
        processPostList(list); // 让个人主页的列表也享受瘦身优化！

        PageInfo<PostVO> pageInfo = new PageInfo<>(list);
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
        // 1. 开启分页拦截
        PageHelper.startPage(pageNum, pageSize);
        
        // 2. 查数据库（SQL 完全不用改，PageHelper 会自动加 Limit）
        List<PostVO> list = postMapper.listLiked(userId);
        
        // 3. 数据瘦身优化（截取首图，清空长文本）
        processPostList(list); 
        
        // 4. 封装成分页对象返回
        PageInfo<PostVO> pageInfo = new PageInfo<>(list);
        return new PageBean<>(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public int softDelete(Integer id, Integer userId) {
        return postMapper.softDelete(id, userId);
    }

    @Override
    public PostVO getPostDetailById(Integer id) {
        return postMapper.getPostDetailById(id);
    }

    @Override
    public List<Integer> getLikedPostIds() {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");
        return userLikesMapper.listLikedPostIds(userId);
    }
}