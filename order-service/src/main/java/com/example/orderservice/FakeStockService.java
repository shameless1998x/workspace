package com.example.orderservice;

import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component // 别忘了交给 Spring 管理
public class FakeStockService {

    // 划重点：这个注解极其强大！
    // 它代表：如果 RabbitMQ 里没有 "stock_queue" 这个队列，它就自动建一个；然后它会一直监听这个队列。
//    @RabbitListener(queuesToDeclare = @Queue("stock_queue"))
//    public void receiveMessage(String message) {
//        System.out.println("====== 后厨接到订单啦！======");
//        System.out.println("【制作小哥看了一眼小票】内容是: " + message);
//        System.out.println("【系统动作】正在扣减库存...");
//        System.out.println("【系统动作】扣减成功！✅");
//        System.out.println("=============================");
//    }
    // 监听扣库存队列
    @RabbitListener(queues = "fanout_stock_queue")
    public void listenStock(String msg) {
        System.out.println("【库存服务】收到广播：正在减扣商品库存... 内容：" + msg);
    }

    // 监听发短信队列
    @RabbitListener(queues = "fanout_sms_queue")
    public void listenSms(String msg) {
        System.out.println("【短信服务】收到广播：正在给用户发送确认短信... 内容：" + msg);
    }
}