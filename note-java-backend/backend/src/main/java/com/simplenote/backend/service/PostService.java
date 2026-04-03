package com.simplenote.backend.service;

import java.util.List;

import com.simplenote.backend.pojo.PageBean;
import com.simplenote.backend.pojo.Post;
import com.simplenote.backend.pojo.PostVO;

public interface PostService {
    void add(Post post);
    //这个后续删掉，改为点进单个帖子详情页的功能
    List<Post> list();

    List<PostVO> listWithAuthor();

    // 点赞或取消点赞 (Toggle逻辑)
    void toggleLike(Integer postId);

    List<PostVO> listOwn(Integer userId);
    List<PostVO> listLiked(Integer userId);

    // 软删除笔记
    int softDelete(Integer id, Integer userId);

    PageBean<PostVO> listWithPage(Integer pageNum, Integer pageSize);

    PostVO getPostDetailById(Integer id);

    // 获取当前用户点赞过的帖子 ID 列表
    List<Integer> getLikedPostIds();

    // 根据用户ID分页查询帖子
    PageBean<PostVO> pageQueryByUser(Integer userId, Integer pageNum, Integer pageSize);
}