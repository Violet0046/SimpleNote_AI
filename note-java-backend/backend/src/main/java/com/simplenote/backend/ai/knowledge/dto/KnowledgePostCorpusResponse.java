package com.simplenote.backend.ai.knowledge.dto;

import lombok.Data;

import java.util.List;

@Data
public class KnowledgePostCorpusResponse {
    private Integer minPostId;
    private Integer maxPostId;
    private Integer totalPosts;
    private Integer maxCommentsPerPost;
    private List<KnowledgePostDocument> posts;
}
