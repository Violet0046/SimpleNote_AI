package com.simplenote.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
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
        // 登录和注册接口不需要“出入证”，其他的都要！
        registry.addInterceptor(loginInterceptor)
                .excludePathPatterns("/user/login", "/user/register");
    }
}
