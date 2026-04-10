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
    // 查询是否已经关注
    @Select("SELECT COUNT(*) FROM follow_user WHERE follower_id = #{followerId} AND followed_id = #{followedId}")
    Integer checkFollowStatus(@Param("followerId") Integer followerId, @Param("followedId") Integer followedId);
    @Select("SELECT followed_id FROM follow_user WHERE follower_id = #{followerId} ORDER BY create_time DESC")
    java.util.List<Integer> findFollowedIdsByFollowerId(@Param("followerId") Integer followerId);

    @Select("SELECT follower_id FROM follow_user WHERE followed_id = #{followedId} ORDER BY create_time DESC")
    java.util.List<Integer> findFollowerIdsByFollowedId(@Param("followedId") Integer followedId);

    @Delete("DELETE FROM follow_user WHERE follower_id = #{followerId}")
    void deleteByFollowerId(@Param("followerId") Integer followerId);

    @Delete({
        "<script>",
        "DELETE FROM follow_user ",
        "WHERE follower_id = #{followerId} ",
        "AND followed_id NOT IN ",
        "<foreach item='followedId' collection='followedIds' open='(' separator=',' close=')'>",
        "#{followedId}",
        "</foreach>",
        "</script>"
    })
    void deleteByFollowerIdAndFollowedIdNotIn(@Param("followerId") Integer followerId,
                                              @Param("followedIds") java.util.List<Integer> followedIds);

    @Insert({
        "<script>",
        "INSERT IGNORE INTO follow_user(follower_id, followed_id, create_time) VALUES ",
        "<foreach item='followedId' collection='followedIds' separator=','>",
        "(#{followerId}, #{followedId}, now())",
        "</foreach>",
        "</script>"
    })
    void batchInsertByFollowerId(@Param("followerId") Integer followerId, @Param("followedIds") java.util.List<Integer> followedIds);
}
