package com.example.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component // 🌟 必须加上这个注解，把它交给 Spring 容器管理
public class AuthFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(AuthFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. 获取请求路径
        String path = exchange.getRequest().getURI().getPath();
        log.info("🔍 有人试图通过网关，访问路径: {}", path);

        // 2. 获取请求参数中的 token (假设我们要求带上 ?token=admin 才能过)
        String token = exchange.getRequest().getQueryParams().getFirst("token");

        // 3. 校验逻辑
        if ("admin".equals(token)) {
            log.info("✅ 发现管理员通行证，放行！");
            return chain.filter(exchange); // 让请求继续往下走
        } else {
            log.info("❌ 没有通行证，拦截请求！");
            // 设置响应状态码为 401 (未授权)
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            // 结束请求，直接返回
            return exchange.getResponse().setComplete();
        }
    }

    /**
     * 设置过滤器的执行顺序。
     * 数字越小，优先级越高（越先执行）。
     */
    @Override
    public int getOrder() {
        return 0; 
    }
}