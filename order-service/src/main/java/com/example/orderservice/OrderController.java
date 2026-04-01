package com.example.orderservice;


import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/order")
public class OrderController {
//    @Autowired
//    public RestTemplate restTemplate;
    @Autowired
    public UserClient userClient;

    @GetMapping("/test")
    public String callDemo() {
//        String result = restTemplate.getForObject("http://demo/user/all", String.class);
//        return "从 order-service 发起调用成功！收到 demo 服务的数据为："+result;

        String allUsers = userClient.findAllUsers();
        return "从 order-service 发起调用成功！收到 demo 服务的数据为："+allUsers;
    }
    // 1. 请出 RabbitMQ 专属快递员
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/testFanout")
    public String testFanout() {
        String message = "用户下单成功，触发后续连锁反应！";
        // 参数1：交换机名字
        // 参数2：路由键（RoutingKey），广播模式下传空字符串 "" 即可
        // 参数3：消息内容
        rabbitTemplate.convertAndSend("order_fanout_exchange", "", message);
        return "广播消息已发出，请观察后厨和短信中心的反应！";
    }
}
