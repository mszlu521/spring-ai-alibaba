package com.mszlu.ai.alibaba.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ToolService {

    private final ChatModel chatModel;

    public ToolService(@Qualifier("dashScopeChatModel") ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    /**
     * 手动执行工具调用
     */
    public String chatWithTools(String userInput, List<ToolCallback> tools) {
        ChatOptions chatOptions = ToolCallingChatOptions.builder()
                .toolCallbacks(tools)
                .internalToolExecutionEnabled(false)
                .build();
        Prompt prompt = new Prompt(
                new UserMessage(userInput),
                chatOptions
        );
        ChatClient chatClient = ChatClient.builder(chatModel).build();
        ToolCallingManager toolCallingManager = ToolCallingManager.builder().build();
        ChatResponse chatResponse = chatClient
                .prompt(prompt)
                .call()
                .chatResponse();
        // 检查 AI 是否要求调用工具
        while (chatResponse != null && chatResponse.hasToolCalls()) {
            System.out.println("AI requires tool calls: " + chatResponse.getResult().getOutput().getToolCalls());
            ToolExecutionResult toolExecutionResult = toolCallingManager.executeToolCalls(prompt, chatResponse);
            prompt = new Prompt(toolExecutionResult.conversationHistory());
            chatResponse = chatClient
                    .prompt(prompt)
                    .call()
                    .chatResponse();
        }
        // 没有工具调用，直接返回 AI 的回复
        return chatResponse == null ? "no result" : chatResponse.getResult().getOutput().getText();
    }
}