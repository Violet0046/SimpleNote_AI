package com.simplenote.backend.security.interceptor;

import com.simplenote.backend.security.context.UserContextHolder;
import com.simplenote.backend.security.jwt.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/user/login",
            "/user/register",
            "/user/info/*",
            "/agent/knowledge/**",
            "/post/list/page",
            "/post/list/user",
            "/post/detail/*",
            "/comment/list",
            "/follow/following/*",
            "/follow/followers/*",
            "/comment/replies",
            "/error"
    );

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public boolean preHandle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler
    ) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String requestUri = request.getRequestURI();
        String token = request.getHeader("Authorization");

        boolean isPublicPath = PUBLIC_PATHS.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, requestUri));

        try {
            if (token != null && !token.isEmpty()) {
                Map<String, Object> claims = JwtUtils.parseToken(token);
                UserContextHolder.set(claims);
            } else if (!isPublicPath) {
                response.setStatus(401);
                return false;
            }

            return true;
        } catch (Exception ex) {
            if (isPublicPath) {
                return true;
            }

            response.setStatus(401);
            return false;
        }
    }

    @Override
    public void afterCompletion(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler,
            @Nullable Exception ex
    ) throws Exception {
        UserContextHolder.remove();
    }
}
