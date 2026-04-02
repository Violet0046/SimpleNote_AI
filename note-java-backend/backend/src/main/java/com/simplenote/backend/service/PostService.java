package com.simplenote.backend.service;

import java.util.List;

import com.simplenote.backend.pojo.PageBean;
import com.simplenote.backend.pojo.Post;
import com.simplenote.backend.pojo.PostVO;

public interface PostService {
    void add(Post post);

    List<Post> list();

    List<PostVO> listWithAuthor();

    // 点赞或取消点赞 (Toggle逻辑)
    void toggleLike(Integer postId);

    List<PostVO> listOwn(Integer userId);
    List<PostVO> listLiked(Integer userId);

    // 软删除笔记
    int softDelete(Integer id, Integer userId);

    PageBean<PostVO> listWithPage(Integer pageNum, Integer pageSize);
}