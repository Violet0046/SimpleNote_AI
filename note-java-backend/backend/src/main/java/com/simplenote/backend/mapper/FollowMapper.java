package com.simplenote.backend.mapper;

import org.apache.ibatis.annotations.*;

@Mapper
public interface FollowMapper {
    
    // 1. 查是否关注过
    @Select("SELECT count(*) FROM follow_user WHERE follower_id = #{followerId} AND followed_id = #{followedId}")
    int isFollowing(@Param("followerId") Integer followerId, @Param("followedId") Integer followedId);

    // 2. 关注操作 (加入 create_time 记录关注时间，并加上 @Param 防止报错)
    @Insert("INSERT INTO follow_user(follower_id, followed_id, create_time) VALUES(#{followerId}, #{followedId}, now())")
    void follow(@Param("followerId") Integer followerId, @Param("followedId") Integer followedId);

    // 3. 取消关注操作
    @Delete("DELETE FROM follow_user WHERE follower_id = #{followerId} AND followed_id = #{followedId}")
    void unfollow(@Param("followerId") Integer followerId, @Param("followedId") Integer followedId);
}