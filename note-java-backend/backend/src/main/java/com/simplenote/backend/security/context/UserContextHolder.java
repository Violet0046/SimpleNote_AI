package com.simplenote.backend.security.context;

import java.util.Map;

public class UserContextHolder {
    private static final ThreadLocal<Map<String, Object>> THREAD_LOCAL = new ThreadLocal<>();

    public static void set(Map<String, Object> claims) {
        THREAD_LOCAL.set(claims);
    }

    public static Map<String, Object> get() {
        return THREAD_LOCAL.get();
    }

    public static void remove() {
        THREAD_LOCAL.remove();
    }
}
