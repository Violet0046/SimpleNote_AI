package com.simplenote.backend.mapper;

import com.simplenote.backend.pojo.Comment;
import com.simplenote.backend.pojo.CommentVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface CommentMapper {
    // 插入一条新评论
    @Insert("INSERT INTO comment(post_id, user_id, content, parent_id, reply_to_user_id, ip_location) " +
            "VALUES(#{postId}, #{userId}, #{content}, #{parentId}, #{replyToUserId}, #{ipLocation})")
    void insert(Comment comment);

    // 一次性捞出该帖子的所有评论（连同作者、被回复人的信息）
    @Select("SELECT c.id, c.user_id, u1.nickname AS author_name, u1.avatar AS author_avatar, " +
            "c.content, c.is_deleted, c.ip_location, c.reply_to_user_id, u2.nickname AS reply_to_user_name, c.create_time, c.parent_id " +
            "FROM comment c " +
            "JOIN user u1 ON c.user_id = u1.id " +
            "LEFT JOIN user u2 ON c.reply_to_user_id = u2.id " +
            "WHERE c.post_id = #{postId} " +
            "ORDER BY c.create_time ASC")
    List<CommentVO> findAllByPostId(Integer postId);

    // 逻辑删除：并不是真的删掉，而是把标志位改成 1
    @Update("UPDATE comment SET is_deleted = 1 WHERE id = #{id} AND user_id = #{userId}")
    int softDelete(Long id, Integer userId);

    @Select("<script>" +
        "SELECT c.*, u1.nickname AS author_name, u1.avatar AS author_avatar, " +
        "u2.nickname AS reply_to_user_name " +
        "FROM comment c " +
        "JOIN user u1 ON c.user_id = u1.id " +
        "LEFT JOIN user u2 ON c.reply_to_user_id = u2.id " +
        "WHERE c.post_id = #{postId} AND c.parent_id = 0 " + // 🌟 这里只查一级评论，子评论后面按需加载
        "<choose>" +
        "  <when test='sortType == 1'> ORDER BY c.create_time DESC </when>" + // 最新
        "  <when test='sortType == 2'> ORDER BY c.likes_count DESC </when>" + // 最热（假设你有点赞数）
        "  <otherwise> ORDER BY c.create_time ASC </otherwise>" + // 默认
        "</choose>" +
        "LIMIT #{offset}, #{pageSize}" + // 🌟 分页核心
        "</script>")
    List<CommentVO> findRootComments(Integer postId, Integer sortType, Integer offset, Integer pageSize);

    // 统计该帖子的总评论数（给前端展示：共1234条评论）
    @Select("SELECT COUNT(*) FROM comment WHERE post_id = #{postId} AND is_deleted = 0")
    Integer countByPostId(Integer postId);
}