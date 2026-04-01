package com.simplenote.backend.pojo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 帖子实体类
 */
@Data
public class Post {
    private Integer id;           // 帖子ID
    private Integer userId;       // 发帖人ID
    private String title;         // 帖子标题
    private String content;       // 帖子正文
    private String images;        // 配图
    private Integer likesCount;   // 点赞数
    private LocalDateTime createTime; // 发布时间
}