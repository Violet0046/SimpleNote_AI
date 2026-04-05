package com.simplenote.backend.config;

import com.simplenote.backend.interceptors.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .excludePathPatterns(
                        "/user/login",      // 放行登录
                        "/user/register",   // 注册
                        "/user/info/*",     // 查看他人主页基本信息
                        "/post/list/page",  // 发现页帖子列表
                        "/post/list/user",  // 进别人的主页看帖子列表
                        "/post/detail/*",   // 帖子详情页 (注意配合 Controller 把路径改为 /post/detail/{id})
                        "/comment/list",    // 游客看帖子下的评论
                        "/upload",
                        "/uploads/**",      // 图片文件访问放行
                        "/**/*.jpg",        // 默认头像等静态资源放行
                        "/**/*.png",
                        "/**/*.jpeg",
                        "/**/*.gif",
                        "/follow/following/*",// 进别人的主页看关注列表
                        "/follow/followers/*",// 进别人的主页看粉丝列表
                        "/follow/following/*",
                        "/follow/followers/*",
                        "/comment/replies",   // 游客看评论的回复列表
                        "/error"              // 错误页
                );
    }

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173") // 前端开发服务器地址
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    // 配置静态资源映射规则
    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // 获取与 FileUploadController 中一致的当前项目目录
        String uploadPath = System.getProperty("user.dir") + "/uploads/";
        
        // 关键映射：把网络请求的 /uploads/** 映射到本地 file: 物理路径
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath);
    }
}