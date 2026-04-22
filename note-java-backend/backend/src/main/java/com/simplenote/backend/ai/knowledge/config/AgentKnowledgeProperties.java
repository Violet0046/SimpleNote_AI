package com.simplenote.backend.ai.knowledge.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@Validated
@ConfigurationProperties(prefix = "agent.knowledge")
public class AgentKnowledgeProperties {
    private Integer minPostId;
    private Integer maxPostId;
    private Integer maxPosts;
    private Integer maxCommentsPerPost;
    private Integer maxContentLength;
    private Integer maxCommentLength;

    public Integer getMinPostId() {
        return minPostId;
    }

    public void setMinPostId(Integer minPostId) {
        this.minPostId = minPostId;
    }

    public Integer getMaxPostId() {
        return maxPostId;
    }

    public void setMaxPostId(Integer maxPostId) {
        this.maxPostId = maxPostId;
    }

    public Integer getMaxPosts() {
        return maxPosts;
    }

    public void setMaxPosts(Integer maxPosts) {
        this.maxPosts = maxPosts;
    }

    public Integer getMaxCommentsPerPost() {
        return maxCommentsPerPost;
    }

    public void setMaxCommentsPerPost(Integer maxCommentsPerPost) {
        this.maxCommentsPerPost = maxCommentsPerPost;
    }

    public Integer getMaxContentLength() {
        return maxContentLength;
    }

    public void setMaxContentLength(Integer maxContentLength) {
        this.maxContentLength = maxContentLength;
    }

    public Integer getMaxCommentLength() {
        return maxCommentLength;
    }

    public void setMaxCommentLength(Integer maxCommentLength) {
        this.maxCommentLength = maxCommentLength;
    }
}
