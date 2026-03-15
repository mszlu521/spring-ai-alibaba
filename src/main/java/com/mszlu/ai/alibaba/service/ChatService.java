package com.mszlu.ai.alibaba.service;

import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * AI 对话服务
 *
 * 这是你的第一个 AI 服务，它可以和 AI 模型对话！
 */
@Service
public class ChatService {

    /**
     * ChatModel 就是 AI 模型的"遥控器"
     * Spring Boot 会自动注入配置好的模型
     */
    private final ChatModel chatModel;

    public ChatService(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    /**
     * 最简单的对话方式
     *
     * @param userInput 用户说的话
     * @return AI 的回复
     */
    public String simpleChat(String userInput) {
        // 直接调用，传入字符串，返回字符串
        return chatModel.call(userInput);
    }

    /**
     * 更完整的对话方式
     *
     * @param userInput 用户说的话
     * @return AI 的回复（包含更多元信息）
     */
    public String advancedChat(String userInput) {
        // 1. 创建用户消息
        UserMessage userMessage = new UserMessage(userInput);
        SystemMessage systemMessage = new SystemMessage("你是一个诗人，可以用诗人的方式回答问题");
        // 2. 创建 Prompt
        Prompt prompt = new Prompt(systemMessage, userMessage);
        // 3. 调用模型
        ChatResponse response = chatModel.call(prompt);

        // 4. 获取 AI 的回复内容
        return response.getResult().getOutput().getText();
    }
}