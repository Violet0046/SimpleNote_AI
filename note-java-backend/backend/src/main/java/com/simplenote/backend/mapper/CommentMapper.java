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

    // 只查一级评论 (parent_id = 0)，支持动态排序！(配合 PageHelper 使用，去掉手动 LIMIT)
    @Select("<script>" +
            "SELECT c.id, c.user_id, u1.nickname AS author_name, u1.avatar AS author_avatar, " +
            "c.content, c.is_deleted, c.ip_location, c.create_time, c.parent_id " +
            // 注意：如果你数据库里有 likes_count 字段，就在上面 SELECT 里加上 c.likes_count
            "FROM comment c " +
            "JOIN user u1 ON c.user_id = u1.id " +
            "WHERE c.post_id = #{postId} AND c.parent_id = 0 " +
            "<choose>" +
            "  <when test='sortType == 1'> ORDER BY c.create_time DESC </when>" + // 最新
            "  <when test='sortType == 2'> ORDER BY c.likes_count DESC </when>" + // 最热 (需要表里有 likes_count)
            "  <otherwise> ORDER BY c.create_time ASC </otherwise>" + // 默认：按时间正序
            "</choose>" +
            "</script>")
    List<CommentVO> findRootComments(@Param("postId") Integer postId, @Param("sortType") Integer sortType);

    // 预览子评论：只查某个一级评论下的前 3 条最新回复
    @Select("SELECT c.id, c.user_id, u1.nickname AS author_name, u1.avatar AS author_avatar, " +
            "c.content, c.is_deleted, c.ip_location, c.reply_to_user_id, u2.nickname AS reply_to_user_name, c.create_time, c.parent_id " +
            "FROM comment c " +
            "JOIN user u1 ON c.user_id = u1.id " +
            "LEFT JOIN user u2 ON c.reply_to_user_id = u2.id " +
            "WHERE c.parent_id = #{parentId} " +
            "ORDER BY c.create_time ASC " +
            "LIMIT 3")
    List<CommentVO> findPreviewChildComments(@Param("parentId") Long parentId);

    // 统计某个一级评论下到底有多少条子评论 (用于前端显示 "展开剩余 97 条回复")
    @Select("SELECT COUNT(*) FROM comment WHERE parent_id = #{parentId} AND is_deleted = 0")
    Integer countChildComments(@Param("parentId") Long parentId);

    // 逻辑删除
    @Update("UPDATE comment SET is_deleted = 1 WHERE id = #{id} AND user_id = #{userId}")
    int softDelete(@Param("id") Long id, @Param("userId") Integer userId);

    // 统计该帖子的总评论数（含子评论）
    @Select("SELECT COUNT(*) FROM comment WHERE post_id = #{postId} AND is_deleted = 0")
    Integer countByPostId(Integer postId);
}