package com.simplenote.backend.service;

import java.util.List;

import com.simplenote.backend.pojo.LikeStateVO;
import com.simplenote.backend.pojo.PageBean;
import com.simplenote.backend.pojo.Post;
import com.simplenote.backend.pojo.PostVO;

public interface PostService {
    // 发布新帖子
    void add(Post post);
    // 查询帖子列表（包含作者信息）
    List<PostVO> listWithAuthor();
    // 分页查询帖子列表（包含作者信息）
    PageBean<PostVO> listWithPage(Integer pageNum, Integer pageSize);
    // 根据用户ID分页查询帖子
    PageBean<PostVO> pageQueryByUser(Integer userId, Integer pageNum, Integer pageSize);
    // 显式设置点赞状态
    LikeStateVO setLikeState(Integer postId, boolean desiredLiked);
    // 获取用户点赞的帖子列表
    PageBean<PostVO> listLiked(Integer userId, Integer pageNum, Integer pageSize);

    // 软删除笔记
    int softDelete(Integer id, Integer userId);
    // 获取笔记详情（包含作者信息和评论列表）
    PostVO getPostDetailById(Integer id);
}
