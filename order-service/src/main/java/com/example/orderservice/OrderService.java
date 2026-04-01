package com.example.orderservice;

import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    // ✨ 核心魔法：开启全局事务
    @GlobalTransactional(name = "test-seata-jpa", rollbackFor = Exception.class)
    public void createOrderTest() {
        // 1. 创建并保存一个订单实体
        Order order = new Order();
        order.setName("Seata测试-" + System.currentTimeMillis());
        orderRepository.save(order);

        // 2. 打印当前事务 ID (XID)，确认事务已由 Seata 接管
        System.out.println(">>> 全局事务开启，XID: " + RootContext.getXID());

        // 3. 故意制造一个“灾难”
//        throw new RuntimeException("⚡ 模拟故障：事务应该回滚，数据不应存入数据库");
    }
}