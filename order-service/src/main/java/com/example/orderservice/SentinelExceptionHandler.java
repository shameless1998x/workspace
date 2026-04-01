package com.example.orderservice;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;

@Component // 必须加这个注解，把这个类交给 Spring 管理
public class SentinelExceptionHandler implements BlockExceptionHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, BlockException e) throws Exception {
        // 1. 设置返回的格式为 JSON，并设置编码防止中文乱码
        response.setContentType("application/json;charset=utf-8");
        // 2. 设置 HTTP 状态码为 429 (Too Many Requests)
        response.setStatus(429);

        // 3. 准备你要返回的具体提示信息（可以根据异常类型细分）
        String message = "系统繁忙，请稍后再试";
        if (e instanceof FlowException) {
            message = "哎呀，当前访问人数太多啦，被 Sentinel 限流了！🏃‍♂️💨";
        } else if (e instanceof DegradeException) {
            message = "服务降级了，请稍后再试！📉";
        }

        // 4. 手写一个简单的 JSON 字符串返回给前端
        String jsonResult = String.format("{\"code\": 429, \"msg\": \"%s\"}", message);

        // 5. 把 JSON 写回浏览器
        PrintWriter out = response.getWriter();
        out.write(jsonResult);
        out.flush();
        out.close();
    }
}