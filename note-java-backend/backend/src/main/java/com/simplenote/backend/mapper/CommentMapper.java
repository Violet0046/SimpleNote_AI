package com.simplenote.backend.mapper;

import com.simplenote.backend.pojo.Comment;
import com.simplenote.backend.pojo.CommentVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface CommentMapper {
    @Insert("INSERT INTO comment(post_id, user_id, content, parent_id, reply_to_user_id, ip_location, create_time) " +
            "VALUES(#{postId}, #{userId}, #{content}, #{parentId}, #{replyToUserId}, #{ipLocation}, now())")
    void insert(Comment comment);

    @Select("SELECT COUNT(*) FROM comment WHERE parent_id = #{parentId} AND is_deleted = 0")
    Integer countChildComments(@Param("parentId") Long parentId);

    @Update("UPDATE comment SET is_deleted = 1 WHERE id = #{id} AND user_id = #{userId}")
    int softDelete(@Param("id") Long id, @Param("userId") Integer userId);

    @Select("SELECT COUNT(*) FROM comment WHERE post_id = #{postId} AND is_deleted = 0")
    Integer countByPostId(Integer postId);

    @Select("SELECT c.id, c.user_id, u1.nickname AS author_name, u1.avatar AS author_avatar, " +
            "c.content, c.is_deleted, c.ip_location, c.create_time, c.parent_id, c.likes_count, 0 AS is_liked " +
            "FROM comment c " +
            "JOIN user u1 ON c.user_id = u1.id " +
            "WHERE c.post_id = #{postId} AND c.parent_id = 0 AND c.is_deleted = 0 " +
            "ORDER BY c.likes_count DESC, c.create_time DESC " +
            "LIMIT #{limit}")
    List<CommentVO> listEvidenceCommentsByPostId(@Param("postId") Integer postId, @Param("limit") Integer limit);

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
            "  <otherwise> ORDER BY c.create_time ASC </otherwise>" +
            "</choose>" +
            "</script>")
    List<CommentVO> findRootComments(
            @Param("postId") Integer postId,
            @Param("sortType") Integer sortType,
            @Param("currentUserId") Integer currentUserId
    );

    @Select("SELECT c.id, c.user_id, u1.nickname AS author_name, u1.avatar AS author_avatar, " +
            "c.content, c.is_deleted, c.ip_location, c.reply_to_user_id, u2.nickname AS reply_to_user_name, c.create_time, c.parent_id, c.likes_count, " +
            "(SELECT COUNT(*) FROM comment_like cl WHERE cl.comment_id = c.id AND cl.user_id = #{currentUserId}) AS is_liked " +
            "FROM comment c " +
            "JOIN user u1 ON c.user_id = u1.id " +
            "LEFT JOIN user u2 ON c.reply_to_user_id = u2.id " +
            "WHERE c.parent_id = #{parentId} " +
            "ORDER BY c.likes_count DESC, c.create_time ASC")
    List<CommentVO> findChildComments(@Param("parentId") Long parentId, @Param("currentUserId") Integer currentUserId);

    @Select("SELECT COUNT(*) FROM comment_like WHERE comment_id = #{commentId} AND user_id = #{userId}")
    int checkLikeStatus(@Param("commentId") Long commentId, @Param("userId") Integer userId);

    @Insert("INSERT INTO comment_like(comment_id, user_id) VALUES(#{commentId}, #{userId})")
    void addLike(@Param("commentId") Long commentId, @Param("userId") Integer userId);

    @Delete("DELETE FROM comment_like WHERE comment_id = #{commentId} AND user_id = #{userId}")
    void removeLike(@Param("commentId") Long commentId, @Param("userId") Integer userId);

    @Update("UPDATE comment SET likes_count = likes_count + #{offset} WHERE id = #{commentId}")
    void updateLikesCount(@Param("commentId") Long commentId, @Param("offset") int offset);

    @Select("SELECT likes_count FROM comment WHERE id = #{commentId}")
    Integer findLikesCountById(@Param("commentId") Long commentId);

    @Select("SELECT user_id FROM post WHERE id = #{postId}")
    Integer getPostAuthorId(Integer postId);

    @Select("SELECT c.id, c.user_id, u1.nickname AS author_name, u1.avatar AS author_avatar, " +
            "c.content, c.is_deleted, c.ip_location, c.reply_to_user_id, u2.nickname AS reply_to_user_name, c.create_time, c.parent_id, c.likes_count, " +
            "(SELECT COUNT(*) FROM comment_like cl WHERE cl.comment_id = c.id AND cl.user_id = #{currentUserId}) AS is_liked " +
            "FROM comment c " +
            "JOIN user u1 ON c.user_id = u1.id " +
            "LEFT JOIN user u2 ON c.reply_to_user_id = u2.id " +
            "WHERE c.parent_id = #{parentId} " +
            "AND (c.user_id = #{postAuthorId} OR c.likes_count > #{likeThreshold}) " +
            "ORDER BY c.likes_count DESC, c.create_time ASC")
    List<CommentVO> findQualifiedPreviewChild(
            @Param("parentId") Long parentId,
            @Param("currentUserId") Integer currentUserId,
            @Param("postAuthorId") Integer postAuthorId,
            @Param("likeThreshold") double likeThreshold
    );
}
