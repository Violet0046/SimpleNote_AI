package com.simplenote.backend.mapper;

import com.simplenote.backend.pojo.User;
import com.simplenote.backend.pojo.UserDetailVO;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

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

    // 修改个人资料
    @Update("UPDATE user SET nickname=#{nickname}, gender=#{gender}, intro=#{intro}, avatar=#{avatar}, update_time=now() " +
            "WHERE id=#{id}")
    void update(User user);

    // 查询我的关注列表（我关注了谁）
    @Select("SELECT u.id, u.nickname, u.avatar, u.intro FROM follow_user f " +
            "JOIN user u ON f.followed_id = u.id " +
            "WHERE f.follower_id = #{userId} " +
            "ORDER BY f.create_time DESC")
    List<UserDetailVO> getFollowingList(Integer userId);

    // 查询我的粉丝列表（谁关注了我）
    @Select("SELECT u.id, u.nickname, u.avatar, u.intro FROM follow_user f " +
            "JOIN user u ON f.follower_id = u.id " +
            "WHERE f.followed_id = #{userId} " +
            "ORDER BY f.create_time DESC")
    List<UserDetailVO> getFollowersList(Integer userId);
}