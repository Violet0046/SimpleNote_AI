package com.simplenote.backend.pojo;
import lombok.Data;
// 用户详情展示VO类
@Data
public class UserDetailVO {
    private Integer id;       // 小红书号
    private String nickname;  // 昵称
    private String avatar;    // 头像
    private String intro;     // 简介
    private Integer gender;    // 性别
    private String ipLocation; // IP属地
    private Integer followingCount; // 关注数
    private Integer followersCount; // 粉丝数
    private Integer likesCount;    // 获赞总数
}