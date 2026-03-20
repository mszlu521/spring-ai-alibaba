package com.mszlu.ai.alibaba.controller;

import com.alibaba.cloud.ai.graph.NodeOutput;
import com.alibaba.cloud.ai.graph.streaming.OutputType;
import com.alibaba.cloud.ai.graph.streaming.StreamingOutput;
import com.mszlu.ai.alibaba.service.AgentService;
import com.mszlu.ai.alibaba.service.ChatService;
import com.mszlu.ai.alibaba.service.StructuredOutputService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Map;

/**
 * AI 对话接口
 * <p>
 * 通过 HTTP 请求和 AI 对话
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;
    private final StructuredOutputService structuredOutputService;
    private final AgentService agentService;

    public ChatController(ChatService chatService, StructuredOutputService structuredOutputService, AgentService agentService) {
        this.chatService = chatService;
        this.structuredOutputService = structuredOutputService;
        this.agentService = agentService;
    }

    /**
     * 简单对话接口
     * <p>
     * 使用方法：
     * POST http://localhost:8080/api/chat/simple
     * Body: {"message": "你好，请介绍一下自己"}
     */
    @PostMapping("/simple")
    public Map<String, String> simpleChat(@RequestBody Map<String, String> request) {
        String userMessage = request.get("message");
        String aiResponse = chatService.simpleChat(userMessage);

        return Map.of(
                "user", userMessage,
                "ai", aiResponse
        );
    }

    /**
     * GET 方式对话（方便浏览器测试）
     * <p>
     * 使用方法：
     * http://localhost:8080/api/chat/ask?message=你好
     */
    @GetMapping("/ask")
    public Map<String, String> ask(@RequestParam String message) {
        String aiResponse = chatService.advancedChat(message);

        return Map.of(
                "user", message,
                "ai", aiResponse
        );
    }

    @GetMapping("/chatWithRole")
    public Map<String, String> chatWithRole(@RequestParam String role, @RequestParam String message) {
        String aiResponse = chatService.chatWithRole(role, message);
        return Map.of(
                "user", message,
                "message", aiResponse
        );
    }

    @GetMapping("/json")
    public StructuredOutputService.Person chatWithJson() {
        return structuredOutputService.extractUserInfo();
    }

    @GetMapping("/math")
    public Map<String, String> chatWithAgent(@RequestParam String message) {
        String aiResponse = agentService.chatWithAgent(message);
        return Map.of(
                "user", message,
                "message", aiResponse
        );
    }

    @GetMapping(value = "/mathStream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatWithAgentStream(@RequestParam String message) {
        Flux<NodeOutput> stream = agentService.chatWithAgentByStream(message);
        stream.subscribe(
                output -> {
                    // 检查是否为 StreamingOutput 类型
                    if (output instanceof StreamingOutput streamingOutput) {
                        OutputType type = streamingOutput.getOutputType();

                        // 处理模型推理的流式输出
                        if (type == OutputType.AGENT_MODEL_STREAMING) {
                            // 流式增量内容，逐步显示
                            System.out.print(streamingOutput.message().getText());
                        } else if (type == OutputType.AGENT_MODEL_FINISHED) {
                            // 模型推理完成，可获取完整响应
                            System.out.println("\n模型输出完成");
                        }

                        // 处理工具调用完成（目前不支持 STREAMING）
                        if (type == OutputType.AGENT_TOOL_FINISHED) {
                            System.out.println("工具调用完成: " + output.node());
                        }

                        // 对于 Hook 节点，通常只关注完成事件（如果Hook没有有效输出可以忽略）
                        if (type == OutputType.AGENT_HOOK_FINISHED) {
                            System.out.println("Hook 执行完成: " + output.node());
                        }
                    }
                },
                error -> System.err.println("错误: " + error),
                () -> System.out.println("Agent 执行完成")
        );
        return stream.map(NodeOutput::node);
    }

    @GetMapping("/memory")
    public Map<String, String> chatWithAgentMemory(@RequestParam String message) {
        String aiResponse = agentService.chatWithMemory(message);
        return Map.of(
                "user", message,
                "message", aiResponse
        );
    }
}
