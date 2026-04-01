package com.example.orderservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/test/rollback")
    public String testRollback() {
        try {
            orderService.createOrderTest();
        } catch (Exception e) {
            return "方法报错了：" + e.getMessage();
        }
        return "成功";
    }
}