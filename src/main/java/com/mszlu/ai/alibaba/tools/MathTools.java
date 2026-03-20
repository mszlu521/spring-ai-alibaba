package com.mszlu.ai.alibaba.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class MathTools {

    @Tool(name = "add", description = "计算两个数的和")
    public double add(
            @ToolParam(description = "第一个数") double a,
            @ToolParam(description = "第二个数") double b
    ) {
        return a + b;
    }

    @Tool(name = "subtract", description = "计算两个数的差")
    public double subtract(@ToolParam(description = "第一个数") double a,
                           @ToolParam(description = "第二个数") double b
    ) {
        return a - b;
    }

    @Tool(name = "multiply", description = "计算两个数的乘积")
    public double multiply(
            @ToolParam(description = "第一个数") double a,
            @ToolParam(description = "第二个数") double b
    ) {
        return a * b;
    }

    @Tool(name = "divide", description = "计算两个数的商")
    public double divide(
            @ToolParam(description = "第一个数") double a,
            @ToolParam(description = "第二个数") double b
    ) {
        if (b == 0) throw new IllegalArgumentException("不能除以0");
        return a / b;
    }

    @Tool(name = "sqrt", description = "计算平方根")
    public double sqrt(@ToolParam(description = "第一个数") double a) {
        return Math.sqrt(a);
    }
}