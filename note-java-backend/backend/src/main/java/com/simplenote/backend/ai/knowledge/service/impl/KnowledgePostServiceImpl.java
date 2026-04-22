package com.simplenote.backend.ai.knowledge.service.impl;

import com.simplenote.backend.ai.knowledge.config.AgentKnowledgeProperties;
import com.simplenote.backend.ai.knowledge.dto.KnowledgeCommentDTO;
import com.simplenote.backend.ai.knowledge.dto.KnowledgePostCorpusResponse;
import com.simplenote.backend.ai.knowledge.dto.KnowledgePostDocument;
import com.simplenote.backend.ai.knowledge.service.KnowledgePostService;
import com.simplenote.backend.mapper.CommentMapper;
import com.simplenote.backend.mapper.PostMapper;
import com.simplenote.backend.pojo.CommentVO;
import com.simplenote.backend.pojo.PostVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class KnowledgePostServiceImpl implements KnowledgePostService {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private AgentKnowledgeProperties properties;

    @Override
    public KnowledgePostCorpusResponse buildCorpus(Integer minPostId, Integer maxPostId, Integer limit) {
        int resolvedMinPostId = minPostId == null ? properties.getMinPostId() : minPostId;
        int resolvedMaxPostId = maxPostId == null ? properties.getMaxPostId() : maxPostId;
        int resolvedLimit = limit == null || limit <= 0
                ? properties.getMaxPosts()
                : Math.min(limit, properties.getMaxPosts());

        List<KnowledgePostDocument> posts = postMapper.listWithAuthor().stream()
                .filter(post -> post.getId() != null)
                .filter(post -> post.getId() >= resolvedMinPostId && post.getId() <= resolvedMaxPostId)
                .limit(resolvedLimit)
                .map(this::toKnowledgePostDocument)
                .toList();

        KnowledgePostCorpusResponse response = new KnowledgePostCorpusResponse();
        response.setMinPostId(resolvedMinPostId);
        response.setMaxPostId(resolvedMaxPostId);
        response.setTotalPosts(posts.size());
        response.setMaxCommentsPerPost(properties.getMaxCommentsPerPost());
        response.setPosts(posts);
        return response;
    }

    private KnowledgePostDocument toKnowledgePostDocument(PostVO post) {
        KnowledgePostDocument document = new KnowledgePostDocument();
        document.setPostId(post.getId());
        document.setUserId(post.getUserId());
        document.setTitle(sanitize(post.getTitle(), 120));
        document.setContent(sanitize(post.getContent(), properties.getMaxContentLength()));
        document.setAuthorName(post.getAuthorName());
        document.setAuthorAvatar(post.getAuthorAvatar());
        document.setLocation(post.getIpLocation());
        document.setCreateTime(post.getCreateTime() == null ? null : post.getCreateTime().format(DATE_TIME_FORMATTER));
        document.setLikesCount(resolveLikeCount(post));
        document.setCommentCount(commentMapper.countByPostId(post.getId()));
        document.setVideo(Boolean.TRUE.equals(post.getIsVideo()));
        document.setTopComments(commentMapper
                .listEvidenceCommentsByPostId(post.getId(), properties.getMaxCommentsPerPost()).stream()
                .map(this::toKnowledgeCommentDTO)
                .toList());
        return document;
    }

    private KnowledgeCommentDTO toKnowledgeCommentDTO(CommentVO comment) {
        KnowledgeCommentDTO dto = new KnowledgeCommentDTO();
        dto.setId(comment.getId());
        dto.setUserId(comment.getUserId());
        dto.setAuthorName(comment.getAuthorName());
        dto.setContent(sanitize(comment.getContent(), properties.getMaxCommentLength()));
        dto.setLikesCount(comment.getLikesCount());
        dto.setCreateTime(comment.getCreateTime() == null ? null : comment.getCreateTime().format(DATE_TIME_FORMATTER));
        return dto;
    }

    private Integer resolveLikeCount(PostVO post) {
        if (post.getLikesCount() != null) {
            return post.getLikesCount();
        }
        return post.getLikeCount();
    }

    private String sanitize(String value, Integer maxLength) {
        if (value == null) {
            return "";
        }

        String normalized = value.replaceAll("\\s+", " ").trim();
        if (normalized.length() <= maxLength) {
            return normalized;
        }
        return normalized.substring(0, maxLength).trim() + "...";
    }
}
