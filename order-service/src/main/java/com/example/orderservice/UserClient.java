package com.example.orderservice;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "demo")
public interface UserClient {
    @GetMapping("/user/all")
    String findAllUsers();
}
