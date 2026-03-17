package com.mszlu.ai.alibaba.controller;

import com.mszlu.ai.alibaba.service.StreamingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/stream")
public class StreamingController {

    private final StreamingService streamingService;

    public StreamingController(StreamingService streamingService) {
        this.streamingService = streamingService;
    }

    /**
     * SSE 流式对话接口
     *
     * 前端使用 EventSource 接收：
     * const eventSource = new EventSource('/api/stream/chat?message=你好');
     * eventSource.onmessage = (event) => {
     *     console.log(event.data);  // 实时接收每个字
     * };
     */
    @GetMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> streamChat(@RequestParam String message) {
        return streamingService.streamChat(message)
                .map(content -> ServerSentEvent.builder(content).build());
    }
}