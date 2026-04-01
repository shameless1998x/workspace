package com.example.orderservice;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class FakeTopicListener {

    @RabbitListener(queues = "topic_stock_queue")
    public void listenStock(String msg) {
        System.out.println("【后厨服务】收到订单指令，准备扣减库存 -> " + msg);
    }

    @RabbitListener(queues = "topic_finance_queue")
    public void listenFinance(String msg) {
        System.out.println("【财务服务】收到退款请求，准备打钱 -> " + msg);
    }
}