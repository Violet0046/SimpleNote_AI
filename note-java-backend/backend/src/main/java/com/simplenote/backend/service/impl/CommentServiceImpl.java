package com.simplenote.backend.service.impl;

import com.simplenote.backend.mapper.CommentMapper;
import com.simplenote.backend.pojo.Comment;
import com.simplenote.backend.pojo.CommentAddDTO;
import com.simplenote.backend.pojo.CommentVO;
import com.simplenote.backend.service.CommentService;
import com.simplenote.backend.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Override
    public void addComment(CommentAddDTO dto) {
        // 1. 拿到当前登录人的 ID
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");

        // 2. 把 DTO 转换成能存入数据库的 Comment 实体类
        Comment comment = new Comment();
        comment.setPostId(dto.getPostId());
        comment.setUserId(userId);
        comment.setContent(dto.getContent());
        // 容错处理：如果前端没传 parentId，默认为 0（一级评论）
        comment.setParentId(dto.getParentId() == null ? 0L : dto.getParentId());
        comment.setReplyToUserId(dto.getReplyToUserId());
        
        // 3. 封印历史快照 IP (这里先默认写死陕西，真实的商业项目会通过解析 HttpServletRequest 里的真实网络 IP 获得)
        comment.setIpLocation("陕西"); 

        // 4. 存入数据库
        commentMapper.insert(comment);
    }

    @Override
    public List<CommentVO> getCommentTree(Integer postId) {
        List<CommentVO> allComments = commentMapper.findAllByPostId(postId);

        // 1. 软删除脱敏处理：屏蔽被删除的评论内容
        for (CommentVO comment : allComments) {
            if (comment.getIsDeleted() != null && comment.getIsDeleted() == 1) {
                comment.setContent("该评论已被删除");
            }
        }

        // 2. 准备两个桶分拣数据
        List<CommentVO> rootComments = new ArrayList<>();
        List<CommentVO> childComments = new ArrayList<>();

        for (CommentVO comment : allComments) {
            if (comment.getParentId() == null || comment.getParentId() == 0L) {
                rootComments.add(comment);
            } else {
                childComments.add(comment);
            }
        }

        // 3. 把子评论按其父 ID 进行分组 (Java 8 Stream 极速处理)
        Map<Long, List<CommentVO>> childrenMap = childComments.stream()
                .collect(Collectors.groupingBy(CommentVO::getParentId));

        // 4. 将子评论列表组装回一级评论中
        for (CommentVO root : rootComments) {
            List<CommentVO> replies = childrenMap.getOrDefault(root.getId(), new ArrayList<>());
            root.setReplies(replies);
        }

        return rootComments;
    }
}