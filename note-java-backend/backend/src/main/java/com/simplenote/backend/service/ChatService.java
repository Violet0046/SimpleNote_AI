package com.simplenote.backend.service;

import com.simplenote.backend.dto.ChatRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class ChatService {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final String PYTHON_API_URL = "http://127.0.0.1:8000/api/rag_chat";
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    /**
     * 处理聊天请求，通过SSE流式返回结果
     * @param request 用户输入的聊天内容
     * @param emitter SSE发射器，用于向客户端推送数据
     */
    public void processChat(ChatRequest request, SseEmitter emitter) {
        executor.submit(() -> {
            try {
                // 1. 调用Python后端的API获取完整响应
                String fullResponse = callPythonApi(request.getText());

                // 2. 将完整响应拆分成字符，模拟流式输出
                simulateStreaming(fullResponse, emitter);

                // 3. 发送完成信号
                emitter.complete();

            } catch (Exception e) {
                try {
                    emitter.send(SseEmitter.event()
                            .name("error")
                            .data("处理请求时发生错误: " + e.getMessage()));
                    emitter.completeWithError(e);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    /**
     * 调用Python后端API获取完整响应
     */
    private String callPythonApi(String userInput) throws Exception {
        try {
            HttpClient client = HttpClient.newHttpClient();
            // 使用JSON格式构建请求体，避免特殊字符问题
            String requestBody = String.format("{\"text\": \"%s\"}", userInput.replace("\"", "\\\""));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(PYTHON_API_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("Python API响应: " + response.body());

                // 解析JSON响应，提取reply字段
                String jsonResponse = response.body();
                // 简单解析，直接提取reply字段的值
                String reply = extractReplyFromJson(jsonResponse);
                System.out.println("提取的回复内容: " + reply);
                return reply;
            } else {
                throw new RuntimeException("Python API返回错误: " + response.statusCode() + ", 内容: " + response.body());
            }
        } catch (Exception e) {
            System.err.println("调用Python API时发生错误: " + e.getMessage());
            throw e;
        }
    }

    /**
     * 从JSON响应中提取reply字段的内容
     */
    private String extractReplyFromJson(String jsonResponse) {
        // 使用简单的字符串提取，因为不想引入额外的依赖
        try {
            System.out.println("原始JSON响应: " + jsonResponse);

            // 检查响应格式
            if (!jsonResponse.contains("\"status\"") || !jsonResponse.contains("\"reply\"")) {
                throw new RuntimeException("JSON响应格式不正确，缺少status或reply字段");
            }

            // 假设格式为: {"status":"success","reply":"..."}
            int startIndex = jsonResponse.indexOf("\"reply\":\"");
            if (startIndex == -1) {
                throw new RuntimeException("未找到reply字段");
            }

            startIndex += 8; // 跳过 "\"reply\":\""
            int endIndex = jsonResponse.indexOf("\"", startIndex);
            if (endIndex == -1) {
                throw new RuntimeException("reply字段格式错误");
            }

            String reply = jsonResponse.substring(startIndex, endIndex);
            System.out.println("成功提取reply内容: " + reply);
            return reply;
        } catch (Exception e) {
            System.err.println("解析JSON失败: " + e.getMessage());
            throw new RuntimeException("解析JSON失败: " + e.getMessage(), e);
        }
    }

    /**
     * 模拟流式输出：将完整响应拆分成字符，每隔50ms发送一个字符
     */
    private void simulateStreaming(String fullResponse, SseEmitter emitter) throws IOException {
        // 移除引号，但保留换行和其他字符
        String cleanResponse = fullResponse.replace("\"", "");

        // 确保内容不为空
        if (cleanResponse == null || cleanResponse.trim().isEmpty()) {
            throw new IOException("Python API返回内容为空");
        }

        // 发送开始信号
        emitter.send(SseEmitter.event()
                .name("start")
                .data("开始流式传输"));

        for (int i = 0; i < cleanResponse.length(); i++) {
            char currentChar = cleanResponse.charAt(i);
            String chunk = String.valueOf(currentChar);

            // 发送当前字符
            emitter.send(SseEmitter.event()
                    .name("message")
                    .data(chunk));

            // 等待50ms，模拟打字机效果
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException("流式传输被中断", e);
            }
        }
    }
}