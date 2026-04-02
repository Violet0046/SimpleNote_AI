package com.simplenote.backend.controller;

import com.simplenote.backend.pojo.PageBean;
import com.simplenote.backend.pojo.Post;
import com.simplenote.backend.pojo.PostVO;
import com.simplenote.backend.pojo.Result;
import com.simplenote.backend.service.PostService;
import com.simplenote.backend.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping("/add")
    // @RequestBody 告诉服务器解析 JSON 塞进 Post 对象
    public Result<String> add(@RequestBody Post post) {
        
        // --- 1. 验明正身：从ThreadLocal拿到真正的登录用户ID ---
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");
        post.setUserId(userId); // 强行覆盖，防止黑客伪造别人发帖

        // --- 2. 核心校验：小红书规则（图是本体，字是点缀） ---
        if (post.getImages() == null || post.getImages().trim().isEmpty()) {
            return Result.error("发布失败：至少需要上传一张图片哦~");
        }
        
        String[] imageArray = post.getImages().split(",");
        if (imageArray.length > 18) {
            return Result.error("发布失败：最多只能上传 18 张图片！");
        }

        // --- 3. 补全信息：没写标题正文就赋空字符串，防止数据库报 Null 异常 ---
        if (post.getTitle() == null) {
            post.setTitle("");
        }
        if (post.getContent() == null) {
            post.setContent("");
        }

        // --- 4. 存数据库 ---
        postService.add(post);

        return Result.success("发布成功，内容正在审核中...");
    }

    @GetMapping("/list")
    public Result<List<PostVO>> list() {
        List<PostVO> posts = postService.listWithAuthor();
        return Result.success(posts);
    }

    // 处理点赞/取消赞的接口
    // 使用 RESTful 风格，路径比如：/post/15/like
    @PostMapping("/{id}/like")
    public Result<String> toggleLike(@PathVariable("id") Integer id) {
        postService.toggleLike(id);
        return Result.success("操作成功");
    }

    // 获取当前登录用户发布的所有笔记
    @GetMapping("/list/own")
    public Result<List<PostVO>> listOwn() {
        // 1. 验明正身
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");

        // 2. 查数据
        List<PostVO> list = postService.listOwn(userId);
        return Result.success(list);
    }

    // 获取当前登录用户点赞过的所有笔记
    @GetMapping("/list/liked")
    public Result<List<PostVO>> listLiked() {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");

        List<PostVO> list = postService.listLiked(userId);
        return Result.success(list);
    }

    // 删除笔记接口
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Integer id) {
        // 获取当前登录人
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");

        // 执行软删除
        int row = postService.softDelete(id, userId); // 记得去 Service 层包装一下调用 Mapper

        return row > 0 ? Result.success("笔记已删除") : Result.error("删除失败或无权删除");
    }

    // 首页分页推荐接口
    @GetMapping("/list/page")
    public Result<PageBean<PostVO>> listPage(
            @RequestParam(defaultValue = "1") Integer pageNum, // 默认第一页
            @RequestParam(defaultValue = "10") Integer pageSize  // 默认每页10条
    ) {
        PageBean<PostVO> pb = postService.listWithPage(pageNum, pageSize);
        return Result.success(pb);
    }

    // 获取单篇帖子详情
    @GetMapping("/{id}")
    public Result<PostVO> getDetail(@PathVariable Integer id) {
        PostVO postVO = postService.getPostDetailById(id); // 请在Service层实现这个调用
        if (postVO == null) {
            return Result.error("帖子不存在或已被删除");
        }
        return Result.success(postVO);
    }
}