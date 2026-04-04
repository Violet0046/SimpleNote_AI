package com.simplenote.backend.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.simplenote.backend.mapper.CommentMapper;
import com.simplenote.backend.pojo.Comment;
import com.simplenote.backend.pojo.CommentAddDTO;
import com.simplenote.backend.pojo.CommentVO;
import com.simplenote.backend.pojo.PageBean;
import com.simplenote.backend.service.CommentService;
import com.simplenote.backend.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Override
    public void addComment(CommentAddDTO dto) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");

        Comment comment = new Comment();
        comment.setPostId(dto.getPostId());
        comment.setUserId(userId);
        comment.setContent(dto.getContent());
        comment.setParentId(dto.getParentId() == null ? 0L : dto.getParentId());
        comment.setReplyToUserId(dto.getReplyToUserId());
        comment.setIpLocation("陕西"); 

        commentMapper.insert(comment);
    }

    @Override
    public void deleteComment(Long id) {
        // 1. 获取当前登录人
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");

        // 2. 调用 Mapper 执行逻辑删除
        int row = commentMapper.softDelete(id, userId);
        if (row == 0) {
            throw new RuntimeException("删除失败或无权删除");
        }
    }
    
    // 评论树分页拉取
    @Override
    public PageBean<CommentVO> getCommentTreePage(Integer postId, Integer pageNum, Integer pageSize, Integer sortType) {
        
        // 开启分页，只拦截一级评论的查询
        PageHelper.startPage(pageNum, pageSize);
        List<CommentVO> rootComments = commentMapper.findRootComments(postId, sortType);

        // 遍历当前页的这一小撮一级评论
        for (CommentVO root : rootComments) {
            
            // 脱敏：如果一级评论被删除了，替换文字
            if (root.getIsDeleted() != null && root.getIsDeleted() == 1) {
                root.setContent("该评论已被删除");
            }

            // 去数据库拉取该评论的【前3条子评论】作为预览
            List<CommentVO> previewChildren = commentMapper.findPreviewChildComments(root.getId());
            
            for (CommentVO child : previewChildren) {
                if (child.getIsDeleted() != null && child.getIsDeleted() == 1) {
                    child.setContent("该评论已被删除");
                }
            }
            root.setReplies(previewChildren);

            // 统计总共有多少条子评论，告诉前端
            // 前端逻辑：如果 childCount > 3，就显示 "展开剩余的 (childCount - 3) 条回复" 按钮
            Integer childCount = commentMapper.countChildComments(root.getId());
            root.setChildCount(childCount);
        }

        // 封装成分页对象返回
        PageInfo<CommentVO> pageInfo = new PageInfo<>(rootComments);
        return new PageBean<>(pageInfo.getTotal(), rootComments);
    }
}