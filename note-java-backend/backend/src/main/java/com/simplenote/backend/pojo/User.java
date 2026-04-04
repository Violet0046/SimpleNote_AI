package com.simplenote.backend.pojo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
public class User {
    private Integer id;           // 用户ID
    private String username;      // 账号
    private String password;      // 密码
    private String nickname;      // 昵称
    private String avatar;        // 头像
    private LocalDateTime createTime; // 注册时间
}