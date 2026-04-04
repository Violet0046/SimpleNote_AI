package com.simplenote.backend.service;

import java.util.List;

import com.simplenote.backend.pojo.CommentAddDTO;
import com.simplenote.backend.pojo.CommentVO;
import com.simplenote.backend.pojo.PageBean;


public interface CommentService {
    // 添加评论
    void addComment(CommentAddDTO dto);
    // 删除评论
    void deleteComment(Long id);
    // 获取评论树（包含分页参数）
    PageBean<CommentVO> getCommentTreePage(Integer postId, Integer pageNum, Integer pageSize, Integer sortType);
    
    PageBean<CommentVO> getRepliesPage(Long parentId, Integer pageNum, Integer pageSize);
    void toggleCommentLike(Long commentId);
}