package com.tqz.alibaba.cloud.gateway;

import com.tqz.alibaba.cloud.gateway.config.loadbalancer.VersionServiceInstanceListSupplierConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.handler.predicate.*;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;

/**
 * <p>
 * gateway启动类
 *
 * zuul与gateway对比：
 *  zuul:基于servlet2.5，在1.0版本中使用阻塞的api，2.0已改为 Netty Client实现。只支持path匹配。
 *  gateway：基于Spring 5、Project Reactor、Spring Boot 2，使用非阻塞式的API。共有13中路由匹配规则。
 *
 * Spring Cloud Gateway 有三大核心概念：Route（路由）、Predicate（谓词）、Filter（过滤器）。
 * 路由谓词工厂的作用是：符合Predicate的条件，就使用该路由的配置，否则就不管。
 * 一共有以下13大工厂，目前官网只介绍了10种：<a herf:"https://cloud.spring.io/spring-cloud-static/spring-cloud-gateway/2.1.0.RELEASE/single/spring-cloud-gateway.html#gateway-request-predicates-factories"></a>
 * <ul>
 *     <li>AfterRoutePredicateFactory 当请求时的时间After配置的时间时，才转发该请求。
 *     示例：- After=2018-08-16T11:34:42.917822900+08:00[Asia/Shanghai]
 *     </li>
 *     <li>BeforeRoutePredicateFactory 当请求时的时间Before配置的时间时，才转发该请求。
 *     示例：- Before=2018-08-16T11:34:42.917822900+08:00[Asia/Shanghai]
 *     </li>
 *     <li>BetweenRoutePredicateFactory 当请求时的时间Between配置的时间段时，才转发该请求。
 *     示例：- Between=2018-08-16T11:34:42.917822900+08:00[Asia/Shanghai], 2028-08-16T11:34:42.917822900+08:00[Asia/Shanghai]</li>
 *     <li>CloudFoundryRouteServiceRoutePredicateFactory 暂未官方文档说明
 *     </li>
 *     <li>CookieRoutePredicateFactory 当请求时携带的Cookie名称及值与配置的名称及值相符时，才转发该请求。
 *     示例：- Cookie=chocolate, ch.p （当且仅当请求带有名为chocolate，并且值符合正则表达式 ch.p 的Cookie时，才转发该请求）</li>
 *     <li>HeaderRoutePredicateFactory 当请求时携带的Header名称及值与配置的名称及值相符时，才转发该请求。
 *     示例：- Header=X-Request-Id, \d+   （当且仅当请求带有名为X-Request-Id，并且值符合正则表达式 \d+ 的Header时，才转发该请求）
 *     </li>
 *     <li>HostRoutePredicateFactory 当请求时名为Host的Header的值与配置的值相符时，才转发该请求。
 *     示例：- Host=**.somehost.org,**.anotherhost.org
 *     </li>
 *     <li>MethodRoutePredicateFactory 当请求时所使用的HTTP方法与配置的请求方法相符时，才转发该请求。
 *     示例：- Method=GET
 *     </li>
 *     <li>PathRoutePredicateFactory 当请求时所访问的路径与配置的路径相匹配时，才转发该请求。
 *     示例：- Path=/foo/{segment},/example/list,/bar/**
 *     </li>
 *     <li>ReadBodyPredicateFactory 暂未官方文档说明
 *     </li>
 *     <li>QueryRoutePredicateFactory 当请求时所带有的参数名称与配置的参数名称相符时，才转发该请求。
 *     示例：- Query=baz  （当且仅当请求带有名为baz的参数，才转发该请求）
 *     </li>
 *     <li>RemoteAddrRoutePredicateFactory 当请求时的IP地址与配置的IP地址相符时，才转发该请求。
 *     示例：- RemoteAddr=192.168.1.1/24
 *     </li>
 *     <li>WeightRoutePredicateFactory 路由组和权重，才转发该请求。
 *     示例：- Weight=weightGroup,3
 *     </li>
 * </ul>
 * </p>
 *
 * @author tianqingzhao
 * @since 2021/3/1 15:36
 * @see AfterRoutePredicateFactory
 * @see BeforeRoutePredicateFactory
 * @see BetweenRoutePredicateFactory
 * @see CloudFoundryRouteServiceRoutePredicateFactory
 * @see CookieRoutePredicateFactory
 * @see HeaderRoutePredicateFactory
 * @see HostRoutePredicateFactory
 * @see MethodRoutePredicateFactory
 * @see PathRoutePredicateFactory
 * @see QueryRoutePredicateFactory
 * @see ReadBodyRoutePredicateFactory
 * @see RemoteAddrRoutePredicateFactory
 * @see WeightRoutePredicateFactory
 */
@SpringBootApplication
//@LoadBalancerClient(value = "account-service", configuration = VersionLoadBalancerConfiguration.class) // 指定服务
@LoadBalancerClients(defaultConfiguration = VersionServiceInstanceListSupplierConfiguration.class) // 所有服务
public class GatewayServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayServiceApplication.class, args);
    }
}
