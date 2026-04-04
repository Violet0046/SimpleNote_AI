package com.simplenote.backend.controller;

import com.simplenote.backend.pojo.CommentAddDTO;
import com.simplenote.backend.pojo.CommentVO;
import com.simplenote.backend.pojo.PageBean;
import com.simplenote.backend.pojo.Result;
import com.simplenote.backend.service.CommentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    // 1. 发布评论
    @PostMapping("/add")
    public Result<String> add(@RequestBody CommentAddDTO commentAddDTO) {
        commentService.addComment(commentAddDTO);
        return Result.success("评论发布成功！");
    }

    // 2. 分页拉取一级评论树 (完美对接前端需求)
    @GetMapping("/list")
    public Result<PageBean<CommentVO>> list(
            @RequestParam Integer postId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "1") Integer sortType // 1:最新 2:最热
    ) {
        PageBean<CommentVO> pageBean = commentService.getCommentTreePage(postId, pageNum, pageSize, sortType);
        return Result.success(pageBean);
    }

    // 3. 删除评论 (修复：移除 Mapper 直接调用，异常交给 Service 抛出)
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        try {
            commentService.deleteComment(id);
            return Result.success("删除成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}