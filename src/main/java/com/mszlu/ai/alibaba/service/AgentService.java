package com.mszlu.ai.alibaba.service;

import com.alibaba.cloud.ai.graph.NodeOutput;
import com.alibaba.cloud.ai.graph.checkpoint.savers.MemorySaver;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import com.mszlu.ai.alibaba.agent.MathAgentConfig;
import com.mszlu.ai.alibaba.agent.MemoryAgentConfig;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * AI 对话服务
 * <p>
 * 这是你的第一个 AI 服务，它可以和 AI 模型对话！
 */
@Service
public class AgentService {

    private final ChatModel chatModel;

    private final MathAgentConfig mathAgentConfig;
    private final MemoryAgentConfig memoryAgentConfig;
    private final MemorySaver chatMemorySaver;

    /**
     * 构造器注入 ChatClient
     * Spring AI 会自动创建并配置好 ChatClient
     */
    public AgentService(@Qualifier("dashScopeChatModel") ChatModel chatModel, MathAgentConfig mathAgentConfig, MemoryAgentConfig memoryAgentConfig, MemorySaver chatMemorySaver) {
        this.chatModel = chatModel;
        this.mathAgentConfig = mathAgentConfig;
        this.memoryAgentConfig = memoryAgentConfig;
        this.chatMemorySaver = chatMemorySaver;
    }

    public String chatWithAgent(String userInput) {
        try {
            return mathAgentConfig.mathAgent(chatModel).call(userInput).getText();
        } catch (GraphRunnerException e) {
            throw new RuntimeException(e);
        }
    }

    public Flux<NodeOutput> chatWithAgentByStream(String userInput) {
        try {
            return mathAgentConfig.mathAgent(chatModel).stream(userInput);
        } catch (GraphRunnerException e) {
            throw new RuntimeException(e);
        }
    }

    public String chatWithMemory(String userInput) {
        try {
            return memoryAgentConfig
                    .chatAgent(chatModel, chatMemorySaver)
                    .call(userInput)
                    .getText();
        } catch (GraphRunnerException e) {
            throw new RuntimeException(e);
        }
    }
}