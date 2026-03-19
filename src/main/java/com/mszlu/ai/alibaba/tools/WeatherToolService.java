package com.mszlu.ai.alibaba.tools;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class WeatherToolService {

    /**
     * 定义天气查询工具的输入
     */
    public record WeatherRequest(
        @ToolParam(description = "城市名称，如：北京、上海") String city,
        @ToolParam(description = "日期，格式：yyyy-MM-dd，默认为今天") String date
    ) {}

    /**
     * 定义天气查询工具的输出
     */
    public record WeatherResponse(
        String city,
        String date,
        int temperature,
        String weather,
        String suggestion
    ) {}

    /**
     * 创建天气工具
     */
    @Bean
    public ToolCallback weatherTool() {
        return FunctionToolCallback.builder("weather_query", this::queryWeather)
            .description("查询指定城市的天气信息")
            .inputType(WeatherRequest.class)
            .build();
    }

    /**
     * 工具的实现逻辑
     */
    private WeatherResponse queryWeather(WeatherRequest request) {
        // 这里调用真实的天气 API
        // 示例中使用模拟数据
        return new WeatherResponse(
            request.city(),
            request.date() != null ? request.date() : "今天",
            25,
            "晴",
            "天气不错，适合外出"
        );
    }
}