package com.mszlu.ai.alibaba.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class StructuredOutputService {

    private final ChatClient chatClient;

    public StructuredOutputService(@Qualifier("dashScopeChatModel") ChatModel chatModel) {
        this.chatClient = ChatClient.builder(chatModel).build();
    }

    /**
     * 定义输出结构
     */
    public record Person(String name, int age) {}

    /**
     * 返回结构化数据
     */
    public Person extractUserInfo() {
        return chatClient.prompt()
                .user("生成一个用户信息：名字张三，年龄18")
                .call()
                .entity(Person.class);
    }
}