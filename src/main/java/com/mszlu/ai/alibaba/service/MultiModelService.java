package com.mszlu.ai.alibaba.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
public class MultiModelService {

    // 注入多个模型
    private final ChatModel dashScopeChatModel;
    private final ChatModel ollamaChatModel;

    public MultiModelService( @Qualifier("dashScopeChatModel") ChatModel dashScopeChatModel,
                             @Qualifier("ollamaChatModel") ChatModel ollamaChatModel) {
        this.dashScopeChatModel = dashScopeChatModel;
        this.ollamaChatModel = ollamaChatModel;
    }

    /**
     * 根据模型名称切换
     */
    public String chatWithModel(String modelName, String message) {
        ChatModel selectedModel = switch (modelName) {
            case "ollama" -> ollamaChatModel;
            default -> dashScopeChatModel;  // 默认使用 DashScope
        };

        ChatClient chatClient = ChatClient.builder(selectedModel).build();
        return chatClient.prompt()
            .user(message)
            .call()
            .content();
    }
}