package com.mszlu.ai.alibaba.controller;

import com.mszlu.ai.alibaba.service.ChatService;
import com.mszlu.ai.alibaba.service.StructuredOutputService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * AI 对话接口
 *
 * 通过 HTTP 请求和 AI 对话
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;
    private final StructuredOutputService structuredOutputService;

    public ChatController(ChatService chatService, StructuredOutputService structuredOutputService) {
        this.chatService = chatService;
        this.structuredOutputService = structuredOutputService;
    }

    /**
     * 简单对话接口
     *
     * 使用方法：
     * POST http://localhost:8080/api/chat/simple
     * Body: {"message": "你好，请介绍一下自己"}
     */
    @PostMapping("/simple")
    public Map<String, String> simpleChat(@RequestBody Map<String, String> request) {
        String userMessage = request.get("message");
        String aiResponse = chatService.simpleChat(userMessage);

        return Map.of(
                "user", userMessage,
                "ai", aiResponse
        );
    }

    /**
     * GET 方式对话（方便浏览器测试）
     *
     * 使用方法：
     * http://localhost:8080/api/chat/ask?message=你好
     */
    @GetMapping("/ask")
    public Map<String, String> ask(@RequestParam String message) {
        String aiResponse = chatService.advancedChat(message);

        return Map.of(
                "user", message,
                "ai", aiResponse
        );
    }

    @GetMapping("/chatWithRole")
    public Map<String, String> chatWithRole(@RequestParam  String role, @RequestParam String message) {
        String aiResponse = chatService.chatWithRole(role, message);
        return Map.of(
                "user", message,
                "message", aiResponse
        );
    }
    @GetMapping("/json")
    public StructuredOutputService.Person chatWithJson() {
        return structuredOutputService.extractUserInfo();
    }
}
