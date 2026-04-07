package com.simplenote.backend.pojo;

import lombok.Data;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

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
    @JsonProperty("isVideo")
    private Boolean isVideo;      // 是否为视频帖子
}