package com.simplenote.backend.mapper;

import com.simplenote.backend.pojo.Post;
import com.simplenote.backend.pojo.PostVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PostMapper {

    // 1. 发布帖子
    @Insert("insert into post(user_id, title, content, images, create_time, is_video) " +
            "values(#{userId}, #{title}, #{content}, #{images}, now(), #{isVideo})")
    void add(Post post);

    // 2. 首页瀑布流查询 (配合 PageHelper 使用，绝对不会全量查)
    @Select("SELECT p.*, " +
            "u.nickname AS authorName, " +
            "u.avatar AS authorAvatar, " +
            "p.likes_count AS likeCount " +
            "FROM post p " +
            "LEFT JOIN user u ON p.user_id = u.id " +
            "WHERE p.is_deleted = 0 " +
            "ORDER BY p.create_time DESC")
    List<PostVO> listWithAuthor();

    // 3. 后端安全校验专用：快速查帖子本身
    @Select("SELECT * FROM post WHERE id = #{id}")
    Post findById(Integer id);

    // 4. 前端详情页专用：查询帖子详细信息（带作者信息）
    @Select("SELECT p.*, " +
            "u.nickname AS authorName, " +
            "u.avatar AS authorAvatar, " +
            "p.likes_count AS likeCount " +
            "FROM post p " +
            "LEFT JOIN user u ON p.user_id = u.id " +
            "WHERE p.id = #{id} AND p.is_deleted = 0")
    PostVO getPostDetailById(Integer id);

    // 5. 合并后的主页接口：根据 userId 查帖子
    @Select("SELECT p.*, " +
            "u.nickname AS authorName, " +
            "u.avatar AS authorAvatar, " +
            "p.likes_count AS likeCount " +
            "FROM post p " +
            "JOIN user u ON p.user_id = u.id " +
            "WHERE p.user_id = #{userId} AND p.is_deleted = 0 " +
            "ORDER BY p.create_time DESC")
    List<PostVO> listByUserId(Integer userId);

    // 6. 个人主页：查询赞过的帖子
    @Select("SELECT p.*, " +
            "u.nickname AS authorName, " +
            "u.avatar AS authorAvatar, " +
            "p.likes_count AS likeCount " +
            "FROM user_likes ul " +
            "JOIN post p ON ul.post_id = p.id " +
            "JOIN user u ON p.user_id = u.id " +
            "WHERE ul.user_id = #{userId} AND p.is_deleted = 0 " +
            "ORDER BY ul.create_time DESC")
    List<PostVO> listLiked(Integer userId);

    // 7. 软删除笔记
    @Update("UPDATE post SET is_deleted = 1 WHERE id = #{id} AND user_id = #{userId}")
    int softDelete(@Param("id") Integer id, @Param("userId") Integer userId);

    // 8. 点赞数 +1
    @Update("UPDATE post SET likes_count = likes_count + 1 WHERE id = #{postId}")
    void incrementLikes(Integer postId);

    // 9. 点赞数 -1
    @Update("UPDATE post SET likes_count = likes_count - 1 WHERE id = #{postId} AND likes_count > 0")
    void decrementLikes(Integer postId);
    @Update("UPDATE post SET likes_count = #{likesCount} WHERE id = #{postId}")
    void updateLikesCount(@Param("postId") Integer postId, @Param("likesCount") Integer likesCount);
    @Select({
        "<script>",
        "SELECT p.*, ",
        "u.nickname AS authorName, ",
        "u.avatar AS authorAvatar, ",
        "p.likes_count AS likeCount ",
        "FROM post p ",
        "JOIN user u ON p.user_id = u.id ",
        "WHERE p.is_deleted = 0 AND p.id IN ",
        "<foreach item='id' collection='postIds' open='(' separator=',' close=')'>",
        "#{id}",
        "</foreach>",
        "</script>"
    })
    List<PostVO> listByIds(@Param("postIds") List<Integer> postIds);

    @Select("SELECT id, likes_count FROM post WHERE user_id = #{userId} AND is_deleted = 0")
    List<Post> findLikeStatsByUserId(@Param("userId") Integer userId);
}
