package com.mszlu.ai.alibaba.agent.interceptors;

import com.alibaba.cloud.ai.graph.agent.interceptor.ModelCallHandler;
import com.alibaba.cloud.ai.graph.agent.interceptor.ModelInterceptor;
import com.alibaba.cloud.ai.graph.agent.interceptor.ModelRequest;
import com.alibaba.cloud.ai.graph.agent.interceptor.ModelResponse;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;

import java.util.List;

// ModelInterceptor - 内容安全检查
public class GuardrailInterceptor extends ModelInterceptor {
    @Override
    public ModelResponse interceptModel(ModelRequest request, ModelCallHandler handler) {
        // 前置：检查输入
        if (containsSensitiveContent(request.getMessages())) {
            return ModelResponse.of(AssistantMessage.builder().content("检测到不适当的内容").build());
        }

        // 执行调用
        ModelResponse response = handler.call(request);

        // 后置：检查输出
        return sanitizeIfNeeded(response);
    }

    @Override
    public String getName() {
        return "GuardrailInterceptor";
    }

    // 输入敏感词（完全禁止）
    private static final List<String> BLOCKED_KEYWORDS = List.of(
            "TMD"
    );


    private InputCheckResult checkInput(List<Message> messages) {
        if (messages == null || messages.isEmpty()) {
            return InputCheckResult.safe();
        }

        StringBuilder allText = new StringBuilder();
        for (Message message : messages) {
            if (message.getText() != null) {
                allText.append(message.getText()).append(" ");
            }
        }

        String text = allText.toString().toLowerCase();

        // 检查禁止词
        for (String keyword : BLOCKED_KEYWORDS) {
            if (text.contains(keyword.toLowerCase())) {
                return InputCheckResult.blocked("包含敏感词: " + keyword);
            }
        }

        return InputCheckResult.safe();
    }

    /**
     * 检查单个文本是否包含敏感内容
     */
    private boolean containsSensitiveContent(List<Message> messages) {
        return checkInput(messages).blocked();
    }

    // ========== 输出处理方法 ==========

    /**
     * 对输出进行安全处理
     */
    private ModelResponse sanitizeIfNeeded(ModelResponse response) {
        return sanitizeOutput(response);
    }

    /**
     * 输入检查结果
     */
    private record InputCheckResult(boolean blocked, boolean warning, String reason) {

        static InputCheckResult safe() {
            return new InputCheckResult(false, false, null);
        }

        static InputCheckResult blocked(String reason) {
            return new InputCheckResult(true, false, reason);
        }

        static InputCheckResult warning(String reason) {
            return new InputCheckResult(false, true, reason);
        }

        boolean hasWarning() {
            return warning;
        }
    }

    /**
     * 清理输出内容中的敏感信息
     */
    private ModelResponse sanitizeOutput(ModelResponse response) {
        if (response == null || response.getMessage() == null) {
            return response;
        }
        if (response.getChatResponse() == null) {
            //工具调用
            return response;
        }
        String originalContent = response.getChatResponse().getResult().getOutput().getText();
        if (originalContent == null || originalContent.isEmpty()) {
            return response;
        }

        String sanitizedContent = originalContent;
        boolean wasSanitized = false;

        // 手机号脱敏：13812345678 → 138****5678
        if (sanitizedContent.matches(".*1[3-9]\\d{9}.*")) {
            sanitizedContent = sanitizedContent.replaceAll(
                    "(1[3-9]\\d)(\\d{4})(\\d{4})",
                    "$1****$3"
            );
            wasSanitized = true;
        }
        if (wasSanitized) {
            return ModelResponse.of(
                    AssistantMessage.builder()
                            .content(sanitizedContent)
                            .build()
            );
        }
        return response;
    }
}

