package com.simplenote.backend.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.simplenote.backend.mapper.PostMapper;
import com.simplenote.backend.mapper.UserLikesMapper;
import com.simplenote.backend.pojo.PageBean;
import com.simplenote.backend.pojo.Post;
import com.simplenote.backend.pojo.PostVO;
import com.simplenote.backend.service.PostService;
import com.simplenote.backend.utils.ThreadLocalUtil;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostServiceImpl implements PostService {
    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserLikesMapper userLikesMapper; // 注入刚才写的 Mapper

    @Override
    public void add(Post post) {
        postMapper.add(post);
    }

    @Override
    public List<Post> list() {
        return postMapper.list();
    }

    @Override
    public List<PostVO> listWithAuthor() {
        return postMapper.listWithAuthor();
    }

    @Transactional  //保证中间表和帖子表的数据一致性
    @Override
    public void toggleLike(Integer postId) {
        Post post = postMapper.findById(postId);
        if (post == null) {
            throw new RuntimeException("点赞失败：该帖子已被删除或不存在！");
        }
        // 1. 获取当前登录用户的 ID 
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");

        // 2. 检查该用户是否已经点过赞
        int count = userLikesMapper.checkUserLike(userId, postId);

        if (count > 0) {
            // 如果已经点过赞 -> 执行【取消赞】逻辑
            userLikesMapper.removeLike(userId, postId); // 删中间表
            postMapper.decrementLikes(postId);          // 帖子总赞数 -1
        } else {
            // 如果还没点过赞 -> 执行【点赞】逻辑
            userLikesMapper.addLike(userId, postId);    // 加中间表
            postMapper.incrementLikes(postId);          // 帖子总赞数 +1
        }
    }

    @Override
    public List<PostVO> listOwn(Integer userId) {
        return postMapper.listOwn(userId);
    }

    @Override
    public List<PostVO> listLiked(Integer userId) {
        return postMapper.listLiked(userId);
    }

    @Override
    public int softDelete(Integer id, Integer userId) {
        // 在真实的复杂业务里，这里还可以加一些额外逻辑
        // 比如：判断这个帖子是不是精华帖，如果是就不让删；或者删帖前记录一下日志
        // 现在咱们直接调用 mapper
        return postMapper.softDelete(id, userId);
    }

    @Override
    public PageBean<PostVO> listWithPage(Integer pageNum, Integer pageSize) {
        // 1. 开启分页魔法！(只对紧跟着它的下一条 SQL 生效)
        PageHelper.startPage(pageNum, pageSize);

        // 2. 直接调用你之前的 Mapper，不需要改任何 SQL！
        // PageHelper 会在底层自动拦截这句调用，给原本的 SQL 加上 LIMIT offset, size
        // 并且它还会自动帮你再发一条 SELECT COUNT(*) 查出总条数！
        List<PostVO> list = postMapper.listWithAuthor();

        // 3. 把查出来的 list 丢进 PageInfo 这个解析器里，它能提取出 total 总数
        PageInfo<PostVO> pageInfo = new PageInfo<>(list);

        // 4. 封装进咱们自己的 PageBean 返回给 Controller
        return new PageBean<>(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public PostVO getPostDetailById(Integer id) {
        // 直接调用 Mapper 层我们刚刚加的那条 SQL
        return postMapper.getPostDetailById(id);
    }
}