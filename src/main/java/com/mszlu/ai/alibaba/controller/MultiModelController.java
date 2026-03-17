package com.mszlu.ai.alibaba.controller;

import com.mszlu.ai.alibaba.service.MultiModelService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
@RestController
@RequestMapping("/api/multi-model")
public class MultiModelController {

    private final MultiModelService multiModelService;
    public MultiModelController(MultiModelService multiModelService) {
        this.multiModelService = multiModelService;
    }
    @GetMapping("/multi-model")
    public Map<String, String> multiModelChat(
            @RequestParam(defaultValue = "dashscope") String model,
            @RequestParam String message) {

        String response = multiModelService.chatWithModel(model, message);

        return Map.of(
                "model", model,
                "response", response
        );
    }
}
