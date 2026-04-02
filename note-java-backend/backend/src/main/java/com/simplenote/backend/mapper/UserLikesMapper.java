package com.simplenote.backend.mapper;

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
    @Insert("INSERT INTO user_likes(user_id, post_id) VALUES(#{userId}, #{postId})")
    void addLike(@Param("userId") Integer userId, @Param("postId") Integer postId);

    // 3. 删除点赞记录
    @Delete("DELETE FROM user_likes WHERE user_id = #{userId} AND post_id = #{postId}")
    void removeLike(@Param("userId") Integer userId, @Param("postId") Integer postId);
}