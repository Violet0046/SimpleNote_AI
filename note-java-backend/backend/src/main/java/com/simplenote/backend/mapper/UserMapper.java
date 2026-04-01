package com.simplenote.backend.mapper;

import com.simplenote.backend.pojo.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    // 1. 根据用户名查询用户（注册前检查用户名是否被占用）
    @Select("select * from user where username = #{username}")
    User findByUsername(String username);

    // 2. 插入新用户（注册）
    @Insert("insert into user(username, password, nickname, create_time) " +
            "values(#{username}, #{password}, #{nickname}, now())")
    void add(User user);
    // 3. 根据用户名和密码查询用户（登录）
    @Select("select * from user where username=#{username} and password=#{password}")
    User findByUsernameAndPassword(String username, String password);
}