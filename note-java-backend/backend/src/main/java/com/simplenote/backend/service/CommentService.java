package com.simplenote.backend.service;

import java.util.List;

import com.simplenote.backend.pojo.CommentAddDTO;
import com.simplenote.backend.pojo.CommentVO;


public interface CommentService {

    void addComment(CommentAddDTO dto);

    List<CommentVO> getCommentTree(Integer postId);
}