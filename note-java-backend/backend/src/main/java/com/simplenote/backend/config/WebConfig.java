package com.simplenote.backend.config;

import com.simplenote.backend.interceptors.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
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
                        "/follow/status/*",
                        "/error"              // 错误页
                );
    }

   @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 将网络路径映射到项目真实物理路径
        String uploadPath = "file:" + System.getProperty("user.dir") + "/uploads/";
        
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadPath);
    }
}