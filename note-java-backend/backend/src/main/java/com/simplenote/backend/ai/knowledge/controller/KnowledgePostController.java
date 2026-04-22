package com.simplenote.backend.ai.knowledge.controller;

import com.simplenote.backend.ai.knowledge.dto.KnowledgePostCorpusResponse;
import com.simplenote.backend.ai.knowledge.service.KnowledgePostService;
import com.simplenote.backend.pojo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/agent/knowledge")
public class KnowledgePostController {
    @Autowired
    private KnowledgePostService knowledgePostService;

    @GetMapping("/posts")
    public Result<KnowledgePostCorpusResponse> getKnowledgePosts(
            @RequestParam(required = false) Integer minPostId,
            @RequestParam(required = false) Integer maxPostId,
            @RequestParam(required = false) Integer limit
    ) {
        return Result.success(knowledgePostService.buildCorpus(minPostId, maxPostId, limit));
    }
}
