package com.simplenote.backend.mapper;

import com.simplenote.backend.pojo.User;
import com.simplenote.backend.pojo.UserDetailVO;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    // 1. 根据用户名查询用户（注册前检查用户名是否被占用）
    @Select("select * from user where username = #{username}")
    User findByUsername(String username);

    // 2. 插入新用户（注册）
    @Insert("insert into user(username, password, nickname, avatar, gender, create_time, update_time) " +
            "values(#{username}, #{password}, #{nickname}, #{avatarUrl}, #{gender}, now(), now())")
    void add(@Param("username") String username, 
             @Param("password") String password, 
             @Param("nickname") String nickname, 
             @Param("gender") Integer gender,
             @Param("avatarUrl") String avatarUrl);
    // 3. 根据用户名和密码查询用户（登录）
    @Select("select * from user where username=#{username} and password=#{password}")
    User findByUsernameAndPassword(String username, String password);

    // 4. 查基本信息（用户详情页所有数据）
    @Select("SELECT " +
            "  u.id, u.nickname, u.avatar, u.intro, u.gender, u.ip_location, " +
            "  (SELECT COUNT(*) FROM follow_user WHERE follower_id = u.id) AS following_count, " +
            "  (SELECT COUNT(*) FROM follow_user WHERE followed_id = u.id) AS followers_count, " +
            "  (SELECT COUNT(*) FROM user_likes l JOIN post p ON l.post_id = p.id WHERE p.user_id = u.id) AS likes_count " +
            "FROM user u " +
            "WHERE u.id = #{id}")
    UserDetailVO getUserDetailById(Integer id);
}