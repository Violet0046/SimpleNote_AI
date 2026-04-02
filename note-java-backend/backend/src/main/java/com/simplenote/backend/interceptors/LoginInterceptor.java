package com.simplenote.backend.interceptors;

import com.simplenote.backend.utils.JwtUtils;
import com.simplenote.backend.utils.ThreadLocalUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

import org.springframework.lang.NonNull;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        System.out.println("【保安拦截检查】当前请求路径：" + request.getRequestURI());
        // OPTIONS：浏览器预检请求
        if ("OPTIONS".equals(request.getMethod())) {
        return true;
        }
        String token = request.getHeader("Authorization");
        try {
            // 1. 解析 Token 拿到 claims (里面有 id 和 username)
            Map<String, Object> claims = JwtUtils.parseToken(token);
            
            // 2. 把 claims 存入当前线程的“笔记本”
            ThreadLocalUtil.set(claims);
            
            return true;
        } catch (Exception e) {
            response.setStatus(401);
            return false;
        }
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, 
        @NonNull HttpServletResponse response, @NonNull Object handler, @org.springframework.lang.Nullable Exception ex) throws Exception {
        ThreadLocalUtil.remove(); 
    }
}