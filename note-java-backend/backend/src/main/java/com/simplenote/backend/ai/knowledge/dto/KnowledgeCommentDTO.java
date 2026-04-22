package com.simplenote.backend.ai.knowledge.dto;

import lombok.Data;

@Data
public class KnowledgeCommentDTO {
    private Long id;
    private Integer userId;
    private String authorName;
    private String content;
    private Integer likesCount;
    private String createTime;
}
