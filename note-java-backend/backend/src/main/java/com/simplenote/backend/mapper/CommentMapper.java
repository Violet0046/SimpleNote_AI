package com.simplenote.backend.mapper;

import com.simplenote.backend.pojo.Comment;
import com.simplenote.backend.pojo.CommentVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentMapper {
    // 插入新评论
    @Insert("INSERT INTO comment(post_id, user_id, content, parent_id, reply_to_user_id, ip_location, create_time) " +
            "VALUES(#{postId}, #{userId}, #{content}, #{parentId}, #{replyToUserId}, #{ipLocation}, now())")
    void insert(Comment comment);

    // 统计某个一级评论下到底有多少条子评论 (用于前端显示 "展开剩余 97 条回复")
    @Select("SELECT COUNT(*) FROM comment WHERE parent_id = #{parentId} AND is_deleted = 0")
    Integer countChildComments(@Param("parentId") Long parentId);

    // 逻辑删除
    @Update("UPDATE comment SET is_deleted = 1 WHERE id = #{id} AND user_id = #{userId}")
    int softDelete(@Param("id") Long id, @Param("userId") Integer userId);

    // 统计该帖子的总评论数（含子评论）
    @Select("SELECT COUNT(*) FROM comment WHERE post_id = #{postId} AND is_deleted = 0")
    Integer countByPostId(Integer postId);

    // 支持最热/最新排序，并联表查询当前用户是否点赞！
    @Select("<script>" +
            "SELECT c.id, c.user_id, u1.nickname AS author_name, u1.avatar AS author_avatar, " +
            "c.content, c.is_deleted, c.ip_location, c.create_time, c.parent_id, c.likes_count, " +
            "(SELECT COUNT(*) FROM comment_like cl WHERE cl.comment_id = c.id AND cl.user_id = #{currentUserId}) AS is_liked " +
            "FROM comment c " +
            "JOIN user u1 ON c.user_id = u1.id " +
            "WHERE c.post_id = #{postId} AND c.parent_id = 0 " +
            "<choose>" +
            "  <when test='sortType == 1'> ORDER BY c.create_time DESC </when>" + 
            "  <when test='sortType == 2'> ORDER BY c.likes_count DESC, c.create_time DESC </when>" + 
            // 防止 sortType 异常时乱序
            "  <otherwise> ORDER BY c.create_time ASC </otherwise>" +
            "</choose>" +
            "</script>")
    List<CommentVO> findRootComments(@Param("postId") Integer postId, @Param("sortType") Integer sortType, @Param("currentUserId") Integer currentUserId);

    // 子评论也支持查询点赞状态，并且优先展示高赞评论！(满足你的需求1)
    @Select("SELECT c.id, c.user_id, u1.nickname AS author_name, u1.avatar AS author_avatar, " +
            "c.content, c.is_deleted, c.ip_location, c.reply_to_user_id, u2.nickname AS reply_to_user_name, c.create_time, c.parent_id, c.likes_count, " +
            "(SELECT COUNT(*) FROM comment_like cl WHERE cl.comment_id = c.id AND cl.user_id = #{currentUserId}) AS is_liked " +
            "FROM comment c " +
            "JOIN user u1 ON c.user_id = u1.id " +
            "LEFT JOIN user u2 ON c.reply_to_user_id = u2.id " +
            "WHERE c.parent_id = #{parentId} " +
            "ORDER BY c.likes_count DESC, c.create_time ASC") // 高赞优先，然后按时间排
    List<CommentVO> findChildComments(@Param("parentId") Long parentId, @Param("currentUserId") Integer currentUserId);

    // 点赞相关操作
    @Select("SELECT COUNT(*) FROM comment_like WHERE comment_id = #{commentId} AND user_id = #{userId}")
    int checkLikeStatus(@Param("commentId") Long commentId, @Param("userId") Integer userId);

    @Insert("INSERT INTO comment_like(comment_id, user_id) VALUES(#{commentId}, #{userId})")
    void addLike(@Param("commentId") Long commentId, @Param("userId") Integer userId);

    @Delete("DELETE FROM comment_like WHERE comment_id = #{commentId} AND user_id = #{userId}")
    void removeLike(@Param("commentId") Long commentId, @Param("userId") Integer userId);

    @Update("UPDATE comment SET likes_count = likes_count + #{offset} WHERE id = #{commentId}")
    void updateLikesCount(@Param("commentId") Long commentId, @Param("offset") int offset);

    // ================= 折叠算法 =================
    
    // 1. 获取帖子作者的 userId
    @Select("SELECT user_id FROM post WHERE id = #{postId}")
    Integer getPostAuthorId(Integer postId);

    // 2. 智能嗅探：只查满足条件（作者本人发出的，或赞数超过阈值）的子评论，并按最高赞排序！
    @Select("SELECT c.id, c.user_id, u1.nickname AS author_name, u1.avatar AS author_avatar, " +
            "c.content, c.is_deleted, c.ip_location, c.reply_to_user_id, u2.nickname AS reply_to_user_name, c.create_time, c.parent_id, c.likes_count, " +
            "(SELECT COUNT(*) FROM comment_like cl WHERE cl.comment_id = c.id AND cl.user_id = #{currentUserId}) AS is_liked " +
            "FROM comment c " +
            "JOIN user u1 ON c.user_id = u1.id " +
            "LEFT JOIN user u2 ON c.reply_to_user_id = u2.id " +
            "WHERE c.parent_id = #{parentId} " +
            "AND (c.user_id = #{postAuthorId} OR c.likes_count > #{likeThreshold}) " +
            "ORDER BY c.likes_count DESC, c.create_time ASC")
    List<CommentVO> findQualifiedPreviewChild(@Param("parentId") Long parentId, @Param("currentUserId") Integer currentUserId, @Param("postAuthorId") Integer postAuthorId, @Param("likeThreshold") double likeThreshold);
}