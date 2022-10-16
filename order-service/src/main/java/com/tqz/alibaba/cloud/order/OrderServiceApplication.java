package com.tqz.alibaba.cloud.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * <p>
 * 订单服务启动类
 * </p>
 *
 * @author tianqingzhao
 * @since 2021/2/26 10:02
 */
@SpringBootApplication(scanBasePackages = {"com.tqz.alibaba.cloud.order", "com.tqz.alibaba.cloud.common"})
@EnableFeignClients(basePackages = {"com.tqz.alibaba.cloud.order.feign"})
@EnableAspectJAutoProxy(exposeProxy = true)
//@EnableBinding({Source.class}) //发送消息
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
