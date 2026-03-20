package com.mszlu.ai.alibaba.agent;

import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.checkpoint.savers.MemorySaver;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MemoryAgentConfig {

    private static final String CHAT_INSTRUCTION = """
            你是一个贴心的聊天助手。
            记住用户告诉你的信息，在后续对话中使用。
            """;

    @Bean
    public MemorySaver chatMemorySaver() {
        return new MemorySaver();  // 内存存储，开发测试用
    }

    @Bean
    public ReactAgent chatAgent(
            @Qualifier("dashScopeChatModel") ChatModel chatModel,
            MemorySaver memorySaver
    ) {

        return ReactAgent.builder()
                .name("ChatAgent")
                .model(chatModel)
                .instruction(CHAT_INSTRUCTION)
                .saver(memorySaver)         // 配置记忆存储
                .enableLogging(true)
                .build();
    }
}