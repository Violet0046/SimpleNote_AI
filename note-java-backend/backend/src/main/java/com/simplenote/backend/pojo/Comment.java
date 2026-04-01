package com.simplenote.backend.pojo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Comment {
    private Integer id;
    private Integer postId;
    private Integer userId;
    private String content;
    private LocalDateTime createTime;
}