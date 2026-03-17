package com.mszlu.ai.alibaba.controller;

import com.mszlu.ai.alibaba.service.ContextChatService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/context")
public class ContextController {

    private final ContextChatService contextChatService;

    public ContextController(ContextChatService contextChatService) {
        this.contextChatService = contextChatService;
    }

    @GetMapping("/chat")
    public Map<String, String> chat(@RequestParam String conversationId,
                                    @RequestParam String message) {
        String aiResponse = contextChatService.chatWithContext(conversationId,message);

        return Map.of(
                "user", message,
                "ai", aiResponse
        );
    }

}
