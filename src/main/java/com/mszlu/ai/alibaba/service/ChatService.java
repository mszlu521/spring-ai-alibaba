package com.mszlu.ai.alibaba.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

    private final ChatClient chatClient;

    /**
     * 构造器注入 ChatClient
     * Spring AI 会自动创建并配置好 ChatClient
     */
    public ChatService(@Qualifier("dashScopeChatModel") ChatModel chatModel) {
        this.chatModel = chatModel;
        this.chatClient = ChatClient.builder(chatModel)
                .defaultSystem("你是一个诗人，请用诗人的方式回答问题.")  // 默认系统提示
                .build();
    }
    /**
     * 使用 Chat Client 进行简单对话
     */
    public String chatWithClient(String userInput) {
        return chatClient.prompt()
                .user(userInput)  // 用户输入
                .call()           // 调用模型
                .content();       // 获取回复内容
    }

    /**
     * 根据角色类型使用不同的系统提示
     */
    public String chatWithRole(String role, String message) {
        String systemPrompt = switch (role) {
            case "teacher" -> "你是一个耐心的老师，用简单的话解释复杂概念。";
            case "poet" -> "你是一个诗人，用优美的诗句回答问题。";
            case "coder" -> "你是一个程序员，回答简洁，多用代码示例。";
            default -> "你是一个 helpful 的助手。";
        };

        return chatClient.prompt()
                .system(systemPrompt)  // 动态设置系统提示
                .user(message)
                .call()
                .content();
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