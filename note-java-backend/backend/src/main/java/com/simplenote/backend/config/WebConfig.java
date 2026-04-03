package com.simplenote.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import com.simplenote.backend.interceptors.LoginInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .excludePathPatterns(
                "/user/login",//放行登录
                "/user/register", //注册
                "/post/list/page", //帖子列表
                "/comment/list",    //游客看帖子下的评论
                "/error", //错误页
                "/post/{id}", //帖子详情页
                "/**/*.jpg",
                "/**/*.png",        
                "/**/*.jpeg",   
                "/**/*.gif", 
                "/post/list/user", //进别人的主页看帖子列表
                "/uploads/**"); //图片访问
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 当访问网络路径 /uploads/** 时...
        registry.addResourceHandler("/uploads/**")
                // ...就把请求映射到本地物理路径 D:/simplenote_uploads/ 下面去！
                // 注意：如果是 Windows，路径前面要加 file: 
                // 如果你上面用的不是 D 盘，这里记得跟着改哦！
                .addResourceLocations("file:D:/simplenote_uploads/");
    }
}
