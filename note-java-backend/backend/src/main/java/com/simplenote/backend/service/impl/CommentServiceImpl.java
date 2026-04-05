package com.simplenote.backend.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.simplenote.backend.mapper.CommentMapper;
import com.simplenote.backend.pojo.Comment;
import com.simplenote.backend.pojo.CommentAddDTO;
import com.simplenote.backend.pojo.CommentVO;
import com.simplenote.backend.pojo.PageBean;
import com.simplenote.backend.service.CommentService;
import com.simplenote.backend.utils.JwtUtils;
import com.simplenote.backend.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private HttpServletRequest request;

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
        Integer myId = getCurrentUserId();
        // 🌟 1. 查出这个帖子是谁发的
        Integer postAuthorId = commentMapper.getPostAuthorId(postId); 

        PageHelper.startPage(pageNum, pageSize);
        List<CommentVO> rootComments = commentMapper.findRootComments(postId, sortType, myId);

        for (CommentVO root : rootComments) {
            if (root.getIsDeleted() != null && root.getIsDeleted() == 1) {
                root.setContent("该评论已被删除");
            }

            // 🌟 2. 动态计算神评门槛：根评论赞数的 25%
            double threshold = (root.getLikesCount() == null ? 0 : root.getLikesCount()) * 0.25;

            // 🌟 3. 极其严苛的限制：只查【最多 1 条】满足条件的子评论！
            PageHelper.startPage(1, 1, false); 
            List<CommentVO> previewChildren = commentMapper.findQualifiedPreviewChild(root.getId(), myId, postAuthorId, threshold);

            for (CommentVO child : previewChildren) {
                if (child.getIsDeleted() != null && child.getIsDeleted() == 1) {
                    child.setContent("该评论已被删除");
                }
            }
            root.setReplies(previewChildren);

            Integer childCount = commentMapper.countChildComments(root.getId());
            root.setChildCount(childCount);
        }

        PageInfo<CommentVO> pageInfo = new PageInfo<>(rootComments);
        return new PageBean<>(pageInfo.getTotal(), rootComments);
    }

    // 获取当前用户 ID (游客为 0)
    private Integer getCurrentUserId() {
        try {
            // 1. 先尝试从 ThreadLocal 获取（走拦截器的普通接口）
            Map<String, Object> map = ThreadLocalUtil.get();
            if (map != null && map.get("id") != null) {
                return (Integer) map.get("id");
            }
            
            // 2. 如果是被拦截器放行的接口（如 /comment/list），我们手动解析请求头里的 Token
            String token = request.getHeader("Authorization");
            if (token != null && !token.isEmpty()) {
                Map<String, Object> claims = JwtUtils.parseToken(token);
                return (Integer) claims.get("id");
            }
        } catch (Exception e) {
            // 解析失败（例如 Token 伪造或过期），静默处理，当做未登录的游客
        }
        return 0; // 0代表未登录
    }

    // 专门分页获取子评论的 Service
    public PageBean<CommentVO> getRepliesPage(Long parentId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<CommentVO> replies = commentMapper.findChildComments(parentId, getCurrentUserId());
        PageInfo<CommentVO> pageInfo = new PageInfo<>(replies);
        return new PageBean<>(pageInfo.getTotal(), replies);
    }

    // 处理点赞逻辑的 Service
    public void toggleCommentLike(Long commentId) {
        Integer userId = getCurrentUserId();
        if (userId == 0) throw new RuntimeException("请先登录");

        int count = commentMapper.checkLikeStatus(commentId, userId);
        if (count > 0) { // 已点赞 -> 取消
            commentMapper.removeLike(commentId, userId);
            commentMapper.updateLikesCount(commentId, -1);
        } else { // 未点赞 -> 增加
            commentMapper.addLike(commentId, userId);
            commentMapper.updateLikesCount(commentId, 1);
        }
    }
}