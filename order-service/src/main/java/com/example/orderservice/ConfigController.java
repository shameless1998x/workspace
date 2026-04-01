package com.example.orderservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
public class ConfigController {
    @Value("${config.welcome:默认欢迎语}")
    private String welcome;

    @GetMapping("/welcome")
    public String getWelcome(){
        return "当前配置内容：" + welcome;
    }
}
