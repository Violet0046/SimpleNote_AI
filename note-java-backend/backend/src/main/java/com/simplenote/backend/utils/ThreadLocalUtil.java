package com.simplenote.backend.utils;

import java.util.Map;

public class ThreadLocalUtil {
    // 声明 ThreadLocal 容器
    private static final ThreadLocal<Map<String, Object>> THREAD_LOCAL = new ThreadLocal<>();

    // 存数据
    public static void set(Map<String, Object> claims) {
        THREAD_LOCAL.set(claims);
    }

    // 取数据
    public static Map<String, Object> get() {
        return THREAD_LOCAL.get();
    }

    // 删数据（非常重要！防止内存泄漏）
    public static void remove() {   
        THREAD_LOCAL.remove();
    }
}