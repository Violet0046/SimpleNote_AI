package com.simplenote.backend.ai.knowledge.service;

import com.simplenote.backend.ai.knowledge.dto.KnowledgePostCorpusResponse;

public interface KnowledgePostService {
    KnowledgePostCorpusResponse buildCorpus(Integer minPostId, Integer maxPostId, Integer limit);
}
