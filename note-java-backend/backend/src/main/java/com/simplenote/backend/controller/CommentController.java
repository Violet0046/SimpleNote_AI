package com.simplenote.backend.controller;

import com.simplenote.backend.mapper.CommentMapper;
import com.simplenote.backend.pojo.CommentAddDTO;
import com.simplenote.backend.pojo.CommentVO;
import com.simplenote.backend.pojo.Result;
import com.simplenote.backend.service.CommentService;
import com.simplenote.backend.utils.ThreadLocalUtil;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;
    @Autowired
    private CommentMapper commentMapper;

    @PostMapping("/add")
    public Result<String> add(@RequestBody CommentAddDTO commentAddDTO) {
        commentService.addComment(commentAddDTO);
        return Result.success("评论发布成功！");
    }

    @GetMapping("/list")
    public Result<List<CommentVO>> list(Integer postId) {
        List<CommentVO> tree = commentService.getCommentTree(postId);
        return Result.success(tree);
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");
        
        // 执行逻辑删除
        int row = commentMapper.softDelete(id, userId);
        return row > 0 ? Result.success("删除成功") : Result.error("删除失败或无权删除");
    }
}