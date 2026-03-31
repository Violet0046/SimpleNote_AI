package com.simplenote.backend.controller;

import com.simplenote.backend.dto.ChatRequest;
import com.simplenote.backend.service.ChatService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * 流式聊天接口
     * 使用POST请求接收用户输入，通过SSE保持连接并流式返回AI响应
     */
    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamChat(@RequestBody ChatRequest request) throws IOException {
        // 创建SSE发射器，设置超时时间为5分钟
        SseEmitter emitter = new SseEmitter(5 * 60 * 1000L);

        // 处理聊天请求
        chatService.processChat(request, emitter);

        return emitter;
    }
}