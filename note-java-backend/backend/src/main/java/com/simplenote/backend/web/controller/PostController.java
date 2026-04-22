package com.simplenote.backend.web.controller;

import com.simplenote.backend.pojo.LikeStateVO;
import com.simplenote.backend.pojo.PageBean;
import com.simplenote.backend.pojo.Post;
import com.simplenote.backend.pojo.PostVO;
import com.simplenote.backend.pojo.Result;
import com.simplenote.backend.security.context.UserContextHolder;
import com.simplenote.backend.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostService postService;

    // 1. 发布帖子
    @PostMapping("/add")
    public Result<String> add(@RequestBody Post post) {
        // --- 1. 验明正身 ---
        Map<String, Object> map = UserContextHolder.get();
        Integer userId = (Integer) map.get("id");
        post.setUserId(userId); 

        // --- 2. 核心校验 ---
        if (post.getImages() == null || post.getImages().trim().isEmpty()) {
            return Result.error("发布失败：至少需要上传一张图片哦~");
        }
        
        String[] imageArray = post.getImages().split(",");
        if (imageArray.length > 18) {
            return Result.error("发布失败：最多只能上传 18 张图片！");
        }

        // --- 3. 补全信息 ---
        if (post.getTitle() == null) post.setTitle("");
        if (post.getContent() == null) post.setContent("");

        // --- 4. 存数据库 ---
        postService.add(post);

        return Result.success("发布成功，内容正在审核中...");
    }

    // 2. 首页分页推荐接口 (发现页)
    @GetMapping("/list/page")
    public Result<PageBean<PostVO>> listPage(
            @RequestParam(defaultValue = "1") Integer pageNum, 
            @RequestParam(defaultValue = "10") Integer pageSize 
    ) {
        PageBean<PostVO> pb = postService.listWithPage(pageNum, pageSize);
        return Result.success(pb);
    }

    // 3. 获取单篇帖子详情
    @GetMapping("/detail/{id}")
    public Result<PostVO> getDetail(@PathVariable Integer id) {
        PostVO postVO = postService.getPostDetailById(id); 
        if (postVO == null) {
            return Result.error("帖子不存在或已被删除");
        }
        return Result.success(postVO);
    }

    // 4. 处理点赞/取消赞
    @PutMapping("/{id}/like")
    public Result<LikeStateVO> like(@PathVariable("id") Integer id) {
        return Result.success(postService.setLikeState(id, true));
    }

    @DeleteMapping("/{id}/like")
    public Result<LikeStateVO> unlike(@PathVariable("id") Integer id) {
        return Result.success(postService.setLikeState(id, false));
    }

    // 5. 获取当前登录用户发布的所有笔记 (修复：加入分页，复用 pageQueryByUser)
    @GetMapping("/list/own")
    public Result<PageBean<PostVO>> listOwn(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "15") Integer pageSize) {
        
        Map<String, Object> map = UserContextHolder.get();
        Integer userId = (Integer) map.get("id");

        // 直接复用查询某个用户帖子的方法，完美！
        PageBean<PostVO> pageBean = postService.pageQueryByUser(userId, pageNum, pageSize);
        return Result.success(pageBean);
    }

    // 6. 根据指定用户ID分页查询帖子 (用于看他人的主页)
    @GetMapping("/list/user")
    public Result<PageBean<PostVO>> getUserPosts(
            @RequestParam Integer userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "15") Integer pageSize) {
        
        PageBean<PostVO> pageBean = postService.pageQueryByUser(userId, pageNum, pageSize);
        return Result.success(pageBean);
    }

    // 7. 获取当前登录用户点赞过的所有笔记 (修复：加入分页参数)
    @GetMapping("/list/liked")
    public Result<PageBean<PostVO>> listLiked(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "15") Integer pageSize) {
        
        Map<String, Object> map = UserContextHolder.get();
        Integer userId = (Integer) map.get("id");

        // 调用升级后的分页方法
        PageBean<PostVO> pageBean = postService.listLiked(userId, pageNum, pageSize);
        return Result.success(pageBean);
    }

    // 8. 删除笔记接口
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Integer id) {
        Map<String, Object> map = UserContextHolder.get();
        Integer userId = (Integer) map.get("id");

        int row = postService.softDelete(id, userId); 
        return row > 0 ? Result.success("笔记已删除") : Result.error("删除失败或无权删除");
    }
}
