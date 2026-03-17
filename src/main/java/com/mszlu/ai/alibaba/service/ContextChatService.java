package com.mszlu.ai.alibaba.service;

import com.mszlu.ai.alibaba.common.InMemoryChatMemory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContextChatService {

    private final ChatClient chatClient;

    public ContextChatService(@Qualifier("dashScopeChatModel") ChatModel chatModel) {
        ChatMemory chatMemory = new InMemoryChatMemory();
        this.chatClient = ChatClient.builder(chatModel)
            .defaultAdvisors(
                    MessageChatMemoryAdvisor.builder(chatMemory).build(),
                    new SafeGuardAdvisor(List.of(
                            "TMD"
                    ))
            )
            .build();
    }

    /**
     * 多轮对话
     * 传入 conversationId 来保持对话上下文
     */
    public String chatWithContext(String conversationId, String message) {
        return chatClient.prompt()
            .user(message)
            .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
            .call()
            .content();
    }
}