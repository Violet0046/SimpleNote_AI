package com.simplenote.backend.mapper;

import com.simplenote.backend.pojo.Post;
import com.simplenote.backend.pojo.PostVO;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface PostMapper {
    // 这里的 #{userId} 等变量，会自动从传入的 Post 对象里取值
    @Insert("insert into post(user_id, title, content, images, create_time) " +
            "values(#{userId}, #{title}, #{content}, #{images}, now())")
    void add(Post post);

    @Select("select * from post where is_deleted = 0 order by create_time desc") 
    List<Post> list();

    @Select("SELECT p.*, " +
            "u.nickname AS authorName, " +
            "u.avatar AS authorAvatar, " + 
            "p.likes_count AS likeCount " +  
            "FROM post p " +
            "LEFT JOIN user u ON p.user_id = u.id " +
            "WHERE p.is_deleted = 0 " + 
            "ORDER BY p.create_time DESC")
    List<PostVO> listWithAuthor();

    // --- 加减赞方法 ---
    @Update("UPDATE post SET likes_count = likes_count + 1 WHERE id = #{postId}")
    void incrementLikes(Integer postId);

    @Update("UPDATE post SET likes_count = likes_count - 1 WHERE id = #{postId} AND likes_count > 0")
    void decrementLikes(Integer postId);

    // 根据 ID 查询帖子 (用于校验帖子是否存在)
    @Select("SELECT * FROM post WHERE id = #{id}")
    Post findById(Integer id);

    // 查询“我的笔记”
    // 直接关联 user 表，把作者的名字和头像查出来，完美适配 PostVO
    @Select("SELECT p.*, u.nickname AS author_name, u.avatar AS author_avatar " +
            "FROM post p " +
            "JOIN user u ON p.user_id = u.id " +
            "WHERE p.user_id = #{userId} AND p.is_deleted = 0 " + 
            "ORDER BY p.create_time DESC")
    List<PostVO> listOwn(Integer userId);

    // 查询“我赞过的笔记”
    // 核心逻辑：从 user_likes 表出发，找到点赞的帖子，再找到帖子的原作者
    @Select("SELECT p.*, u.nickname AS author_name, u.avatar AS author_avatar " +
            "FROM user_likes ul " +
            "JOIN post p ON ul.post_id = p.id " +
            "JOIN user u ON p.user_id = u.id " +
            "WHERE ul.user_id = #{userId} " +
            "ORDER BY ul.create_time DESC")
    List<PostVO> listLiked(Integer userId);

    // 软删除自己的笔记
    @Update("UPDATE post SET is_deleted = 1 WHERE id = #{id} AND user_id = #{userId}")
    int softDelete(Integer id, Integer userId);
}