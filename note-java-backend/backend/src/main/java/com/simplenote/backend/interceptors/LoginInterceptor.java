package com.simplenote.backend.interceptors;

import com.simplenote.backend.utils.JwtUtils;
import com.simplenote.backend.utils.ThreadLocalUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;
import java.util.Objects;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.lang.NonNull;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    // 1. 将原来在 WebConfig 里的 API 白名单转移到这里
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/user/login",      // 登录
            "/user/register",   // 注册
            "/user/info/*",     // 查看他人主页基本信息
            "/post/list/page",  // 发现页帖子列表
            "/post/list/user",  // 进别人的主页看帖子列表
            "/post/detail/*",   // 帖子详情页
            "/comment/list",    // 游客看帖子下的评论
            "/follow/following/*", // 进别人的主页看关注列表
            "/follow/followers/*", // 进别人的主页看粉丝列表
            "/comment/replies", // 游客看评论的回复列表
            "/error"           // 错误页
    );

    // Spring 自带的路径匹配器，完美支持 /* 和 /** 语法
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        // OPTIONS：浏览器预检请求直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        
        String requestURI = request.getRequestURI();
        if (requestURI == null) {
            requestURI = "";
        }
        final String finalRequestURI = requestURI;
        String token = request.getHeader("Authorization");

        // 2. 检查当前请求的路径是否匹配白名单中的任意一项
        boolean isPublicPath = PUBLIC_PATHS.stream()
                .anyMatch(pattern -> pathMatcher.match(Objects.requireNonNull(pattern), finalRequestURI));

        try {
            // 3. 核心逻辑：只要带了 Token，就强行解析！（不管是不是公共接口）
            if (token != null && !token.isEmpty()) {
                Map<String, Object> claims = JwtUtils.parseToken(token);
                // 解析成功，存入当前线程，供后续 Service 使用
                ThreadLocalUtil.set(claims);
            } else {
                // 没带 Token：如果是私有接口（如点赞、发帖）则拦截；公共接口则当做游客放行
                if (!isPublicPath) {
                    response.setStatus(401);
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            // 4. Token 失效或被伪造：公共接口当做未登录游客放行；私有接口拦截报错
            if (isPublicPath) {
                return true;
            } else {
                response.setStatus(401);
                return false;
            }
        }
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, 
        @NonNull HttpServletResponse response, @NonNull Object handler, @org.springframework.lang.Nullable Exception ex) throws Exception {
        // 清理当前线程的数据，防止内存泄漏或串号
        ThreadLocalUtil.remove(); 
    }
}