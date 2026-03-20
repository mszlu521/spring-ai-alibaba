package com.mszlu.ai.alibaba.agent;

import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.agent.hook.modelcalllimit.ModelCallLimitHook;
import com.mszlu.ai.alibaba.agent.hooks.LoggingHook;
import com.mszlu.ai.alibaba.agent.interceptors.GuardrailInterceptor;
import com.mszlu.ai.alibaba.tools.MathTools;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MathAgentConfig {

    private final MathTools mathTools;
    private static final String MATH_INSTRUCTION = """
            你是一个数学专家助手，擅长利用工具解决各种数学问题。
            """;

    public MathAgentConfig(MathTools mathTools) {
        this.mathTools = mathTools;
    }

    @Bean
    public ReactAgent mathAgent(
            @Qualifier("dashScopeChatModel") ChatModel chatModel) {
        return ReactAgent.builder()
                .name("MathAgent")
                .model(chatModel)
                .instruction(MATH_INSTRUCTION)
                .methodTools(mathTools) //注册工具
                .hooks(
                        ModelCallLimitHook
                                .builder()
                                .runLimit(10)
                                .build(),
                        new LoggingHook()
                )   // 限制最多调用 10 次
                .interceptors(new GuardrailInterceptor())
                .enableLogging(true)
                .build();
    }
}