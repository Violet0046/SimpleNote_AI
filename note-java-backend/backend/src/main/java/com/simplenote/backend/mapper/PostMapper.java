package com.simplenote.backend.mapper;

import com.simplenote.backend.pojo.Post;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PostMapper {
    // 这里的 #{userId} 等变量，会自动从传入的 Post 对象里取值
    @Insert("insert into post(user_id, title, content, images, create_time) " +
            "values(#{userId}, #{title}, #{content}, #{images}, now())")
    void add(Post post);
}