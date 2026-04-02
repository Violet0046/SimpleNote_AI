package com.simplenote.backend.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface FollowMapper {
    
    // 查是否关注过（返回大于0表示已关注）
    @Select("SELECT count(*) FROM follow_user WHERE follower_id = #{followerId} AND followed_id = #{followedId}")
    int isFollowing(Integer followerId, Integer followedId);

    // 关注操作
    @Insert("INSERT INTO follow_user(follower_id, followed_id) VALUES(#{followerId}, #{followedId})")
    void follow(Integer followerId, Integer followedId);

    // 取消关注操作
    @Delete("DELETE FROM follow_user WHERE follower_id = #{followerId} AND followed_id = #{followedId}")
    void unfollow(Integer followerId, Integer followedId);
}