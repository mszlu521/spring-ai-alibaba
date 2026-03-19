package com.mszlu.ai.alibaba.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class CalculatorTools {

    /**
     * 加法工具
     * @Tool 注解将这个方法注册为 AI 可调用的工具
     */
    @Tool(name = "add", description = "计算两个数的和")
    public double add(
            @ToolParam(description = "第一个数字") double a,
            @ToolParam(description = "第二个数字") double b) {
        return a + b;
    }

    /**
     * 乘法工具
     */
    @Tool(name = "multiply", description = "计算两个数的乘积")
    public double multiply(
            @ToolParam(description = "第一个数字") double a,
            @ToolParam(description = "第二个数字") double b) {
        return a * b;
    }

    /**
     * 复杂一点的工具：计算 BMI
     */
    @Tool(name = "calculate_bmi", description = "计算身体质量指数(BMI)")
    public String calculateBMI(
            @ToolParam(description = "身高(米)") double height,
            @ToolParam(description = "体重(公斤)") double weight) {
        
        double bmi = weight / (height * height);
        String category;
        
        if (bmi < 18.5) category = "偏瘦";
        else if (bmi < 24) category = "正常";
        else if (bmi < 28) category = "偏胖";
        else category = "肥胖";
        
        return String.format("BMI: %.2f (%s)", bmi, category);
    }
}