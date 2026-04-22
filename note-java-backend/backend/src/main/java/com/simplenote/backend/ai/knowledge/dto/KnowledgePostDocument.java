package com.simplenote.backend.ai.knowledge.dto;

import lombok.Data;

import java.util.List;

@Data
public class KnowledgePostDocument {
    private Integer postId;
    private Integer userId;
    private String title;
    private String content;
    private String authorName;
    private String authorAvatar;
    private String location;
    private String createTime;
    private Integer likesCount;
    private Integer commentCount;
    private Boolean video;
    private List<KnowledgeCommentDTO> topComments;
}
