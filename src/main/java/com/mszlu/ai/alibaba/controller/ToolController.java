package com.mszlu.ai.alibaba.controller;

import com.mszlu.ai.alibaba.service.ToolService;
import com.mszlu.ai.alibaba.tools.CalculatorTools;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * AI 对话接口
 *
 * 通过 HTTP 请求和 AI 对话
 */
@RestController
@RequestMapping("/api/tool")
public class ToolController {

    private final ToolService toolService;
    private final List<ToolCallback> toolCallbacks;
    private final CalculatorTools calculatorTools;

    public ToolController(ToolService toolService, List<ToolCallback> toolCallbacks, CalculatorTools calculatorTools) {
        this.toolService = toolService;
        this.toolCallbacks = toolCallbacks;
        this.calculatorTools = calculatorTools;
    }

    @GetMapping("/cal")
    public Map<String, String> cal(@RequestParam String message) {
        ToolCallback[] callbacks = ToolCallbacks.from(calculatorTools);
        String aiResponse = toolService.chatWithTools(
                message,
                List.of(callbacks)
        );
        return Map.of(
                "user", message,
                "ai", aiResponse
        );
    }

    @GetMapping("/weather")
    public Map<String, String> weather(@RequestParam String message) {
        String aiResponse = toolService.chatWithTools(
                message,
                toolCallbacks
        );
        return Map.of(
                "user", message,
                "ai", aiResponse
        );
    }

}
