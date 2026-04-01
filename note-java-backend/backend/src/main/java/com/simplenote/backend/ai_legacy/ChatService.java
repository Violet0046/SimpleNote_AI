package com.simplenote.backend.ai_legacy;

//import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simplenote.backend.ai_legacy.dto.ChatRequest;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//@Service
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
    /**
     * 调用Python后端API获取完整响应
     */
    private String callPythonApi(String userInput) throws Exception {
        try {
            // 【关键修复 1】强迫 Java 使用 HTTP/1.1 协议！防止 Python Uvicorn 丢失请求体
            HttpClient client = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .build();

            // 【关键修复 2】使用 objectMapper 规范、安全地打包 JSON，绝对不会出错
            java.util.Map<String, String> bodyMap = new java.util.HashMap<>();
            bodyMap.put("text", userInput);
            String requestBody = objectMapper.writeValueAsString(bodyMap);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(PYTHON_API_URL))
                    .header("Content-Type", "application/json; charset=utf-8")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("Python API响应: " + response.body());
                // 这里会调用你刚才修复好的、极其优雅的 extractReplyFromJson 方法
                String reply = extractReplyFromJson(response.body());
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
        try {
            // 使用之前被闲置的 objectMapper 优雅、安全地解析 JSON
            return objectMapper.readTree(jsonResponse).get("reply").asText();
        } catch (Exception e) {
            System.err.println("解析JSON失败: " + e.getMessage());
            throw new RuntimeException("解析JSON失败: " + e.getMessage(), e);
        }
    }

    /**
     * 模拟流式输出：将完整响应拆分成字符，每隔50ms发送一个字符
     */
    private void simulateStreaming(String fullResponse, SseEmitter emitter) throws IOException {
        String cleanResponse = fullResponse.trim();

        if (cleanResponse == null || cleanResponse.isEmpty()) {
            throw new IOException("Python API返回内容为空");
        }

        emitter.send(SseEmitter.event()
                .name("message")
                .data("[START]"));

        // 【核心修复】：将字符串转换为 Unicode 代码点数组，完美保护 Emoji 不被劈开！
        int[] codePoints = cleanResponse.codePoints().toArray();
        
        for (int codePoint : codePoints) {
            String chunk;
            
            if (codePoint == '\n') {
                chunk = "<br>";
            } else if (codePoint == '\r') {
                continue; // 忽略回车符
            } else {
                // 将完整的代码点还原成字符串（无论是普通汉字还是 32位 Emoji 都能完美还原）
                chunk = new String(Character.toChars(codePoint));
            }

            emitter.send(chunk);

            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException("流式传输被中断", e);
            }
        }
        
        emitter.send("[DONE]");
    }
}