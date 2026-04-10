package com.simplenote.backend.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserLikesMapper {
    
    // 1. 查询当前用户是否已经给这篇帖子点过赞
    @Select("SELECT count(*) FROM user_likes WHERE user_id = #{userId} AND post_id = #{postId}")
    int checkUserLike(@Param("userId") Integer userId, @Param("postId") Integer postId);

    // 2. 添加点赞记录
    @Insert("INSERT INTO user_likes(user_id, post_id, create_time) VALUES(#{userId}, #{postId}, now())")
    void addLike(@Param("userId") Integer userId, @Param("postId") Integer postId);

    // 3. 删除点赞记录
    @Delete("DELETE FROM user_likes WHERE user_id = #{userId} AND post_id = #{postId}")
    void removeLike(@Param("userId") Integer userId, @Param("postId") Integer postId);

    // 批量查询当前用户在给定帖子ID列表中点赞了哪些
    @Select({
        "<script>",
        "SELECT post_id FROM user_likes WHERE user_id = #{userId} AND post_id IN ",
        "<foreach item='id' collection='postIds' open='(' separator=',' close=')'>",
        "#{id}",
        "</foreach>",
        "</script>"
    })
    List<Integer> checkUserLikesInBatch(@Param("userId") Integer userId, @Param("postIds") List<Integer> postIds);

    @Select("SELECT post_id FROM user_likes WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<Integer> findPostIdsByUserId(@Param("userId") Integer userId);

    @Select("SELECT user_id FROM user_likes WHERE post_id = #{postId}")
    List<Integer> findUserIdsByPostId(@Param("postId") Integer postId);

    @Delete("DELETE FROM user_likes WHERE post_id = #{postId}")
    void deleteByPostId(@Param("postId") Integer postId);

    @Delete({
        "<script>",
        "DELETE FROM user_likes ",
        "WHERE post_id = #{postId} ",
        "AND user_id NOT IN ",
        "<foreach item='userId' collection='userIds' open='(' separator=',' close=')'>",
        "#{userId}",
        "</foreach>",
        "</script>"
    })
    void deleteByPostIdAndUserIdNotIn(@Param("postId") Integer postId, @Param("userIds") List<Integer> userIds);

    @Insert({
        "<script>",
        "INSERT IGNORE INTO user_likes(user_id, post_id, create_time) VALUES ",
        "<foreach item='userId' collection='userIds' separator=','>",
        "(#{userId}, #{postId}, now())",
        "</foreach>",
        "</script>"
    })
    void batchInsertByPostId(@Param("postId") Integer postId, @Param("userIds") List<Integer> userIds);
}
