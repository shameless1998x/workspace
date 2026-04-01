package com.example.orderservice;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    // --- 1. 正常的业务配置 ---
    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange("order_exchange");
    }

    @Bean
    public Queue orderQueue() {
        return QueueBuilder.durable("order_queue")
                // 【关键：标签1】指定死信交换机
                .withArgument("x-dead-letter-exchange", "dlx_exchange")
                // 【关键：标签2】指定死信路由键
                .withArgument("x-dead-letter-routing-key", "dlx_key")
                .build();
    }

    // --- 2. 死信配置（隔离区） ---
    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange("dlx_exchange");
    }

    @Bean
    public Queue dlqQueue() {
        return new Queue("dlq_queue"); // 这就是存放“毒药消息”的仓库
    }

    @Bean
    public Binding bindDLX(Queue dlqQueue, DirectExchange dlxExchange) {
        return BindingBuilder.bind(dlqQueue).to(dlxExchange).with("dlx_key");
    }
}