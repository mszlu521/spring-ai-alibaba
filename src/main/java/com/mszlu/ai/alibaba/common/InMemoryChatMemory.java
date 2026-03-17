package com.mszlu.ai.alibaba.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class InMemoryChatMemory implements ChatMemory {

    /**
     * 线程安全的对话消息存储。
     * 键：对话ID，值：按顺序排列的消息列表
     */
    private final Map<String, List<Message>> memoryStore = new ConcurrentHashMap<>();

    /**
     * 创建一个新的空内存对话存储。
     */
    public InMemoryChatMemory() {
        // 无需初始化
    }

    /**
     * {@inheritDoc}
     * <p>将消息保存到内存中。如果对话已存在，则追加到历史记录末尾。</p>
     *
     * @throws IllegalArgumentException 如果对话ID为空或消息列表为null
     */
    @Override
    public void add(String conversationId, List<Message> messages) {
        Assert.hasText(conversationId, "conversationId 不能为空");
        Assert.notNull(messages, "messages 不能为 null");

        // 使用原子操作处理并发修改
        memoryStore.compute(conversationId, (id, existingMessages) -> {
            List<Message> updatedList = new ArrayList<>();
            if (existingMessages != null) {
                updatedList.addAll(existingMessages);
            }
            updatedList.addAll(messages);
            return updatedList;
        });
    }

    /**
     * {@inheritDoc}
     * <p>返回消息的不可修改视图，防止外部修改内部状态。</p>
     *
     * @return 对话的消息列表，如未找到则返回空列表
     */
    @Override
    public List<Message> get(String conversationId) {
        Assert.hasText(conversationId, "conversationId 不能为空");

        List<Message> messages = memoryStore.get(conversationId);
        return messages != null
                ? Collections.unmodifiableList(new ArrayList<>(messages))
                : Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     * <p>删除指定对话的所有消息。</p>
     */
    @Override
    public void clear(String conversationId) {
        Assert.hasText(conversationId, "conversationId 不能为空");
        memoryStore.remove(conversationId);
    }

    /**
     * 获取内存中存储的所有对话ID。
     *
     * @return 不可修改的对话ID列表
     */
    public List<String> getConversationIds() {
        return List.copyOf(memoryStore.keySet());
    }

    /**
     * 获取指定对话存储的消息数量。
     *
     * @param conversationId 对话ID
     * @return 消息数量，如对话不存在则返回0
     * @throws IllegalArgumentException 如果对话ID为空
     */
    public int size(String conversationId) {
        Assert.hasText(conversationId, "conversationId 不能为空");
        List<Message> messages = memoryStore.get(conversationId);
        return messages != null ? messages.size() : 0;
    }

    /**
     * 清空内存中的所有对话。
     */
    public void clearAll() {
        memoryStore.clear();
    }

    /**
     * 检查指定对话是否存在于内存中。
     *
     * @param conversationId 要检查的对话ID
     * @return 存在返回true，否则返回false
     */
    public boolean contains(String conversationId) {
        Assert.hasText(conversationId, "conversationId 不能为空");
        return memoryStore.containsKey(conversationId);
    }

}