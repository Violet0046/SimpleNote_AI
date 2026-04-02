package com.simplenote.backend.pojo;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Comment {
    private Long id;
    private Integer postId;
    private Integer userId;
    private String content;
    private Long parentId;
    private Integer replyToUserId;
    private String ipLocation;       // 定格发布时的IP
    private Integer isDeleted;
    private LocalDateTime createTime;
}