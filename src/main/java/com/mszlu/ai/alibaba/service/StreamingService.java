package com.mszlu.ai.alibaba.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class StreamingService {
    private final ChatClient chatClient;

    public StreamingService(@Qualifier("dashScopeChatModel")  ChatModel chatModel) {
        this.chatClient = ChatClient.builder(chatModel).build();
    }

    /**
     * 流式对话
     * 返回 Flux<String>，可以实时接收 AI 的回复
     */
    public Flux<String> streamChat(String message) {
        return chatClient.prompt()
                .user(message)
                .stream()           // 使用 stream() 代替 call()
                .content();         // 返回流式内容
    }
}
