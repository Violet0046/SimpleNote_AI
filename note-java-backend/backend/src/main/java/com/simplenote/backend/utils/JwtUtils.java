package com.simplenote.backend.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.Map;

public class JwtUtils {
    // 秘钥
    private static final String KEY = "SimpleNoteAISecretKey";
    // 过期时间：12小时
    private static final long EXPIRE = 1000 * 60 * 60 * 12;

    // 1. 签发 Token（发证）
    public static String createToken(Map<String, Object> claims) {
        return Jwts.builder()
                .addClaims(claims) // 证件信息：比如用户ID、用户名
                .signWith(SignatureAlgorithm.HS256, KEY) // 盖章
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE)) // 加上有效期
                .compact();
    }

    // 2. 解析 Token（验证）
    public static Map<String, Object> parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(KEY)
                .parseClaimsJws(token)
                .getBody();
    }
}