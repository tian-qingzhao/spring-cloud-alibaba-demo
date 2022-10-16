package com.tqz.alibaba.cloud.account;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.AbstractSentinelInterceptor;
import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.annotation.aspectj.SentinelResourceAspect;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.event.EventPublishingRunListener;
import org.springframework.cloud.bootstrap.BootstrapApplicationListener;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * <p>
 *  <li>1.启动Nacos   15010162490</li>
 *  <li>2.启动Sentinel，
 *  命令：java -jar -Dserver.port=8888 -Dcsp.sentinel.dashboard.server=localhost:8888 -Dproject.name=sentinel-dashboard
 *  sentinel-dashboard-1.8.0.jar
 *  Sentinel在Web环境下遇到的问题：web环境下可以不使用 {@link SentinelResource} 注解，
 *  通过 {@link AbstractSentinelInterceptor} 拦截器拦截接口的请求路径，sentinel定义的resource一定要有 / 为前缀，
 *  例如定义的接口为 account/getByCode，拦截器处理的时候会处理成 /account/getByCode，
 *  sentinel定义的resource必须是 /account/getByCode，否则会不匹配。
 *  可以通过实现 {@link BlockExceptionHandler} 接口实现自定义返回格式。
 *  如果既有web环境又使用了 {@link SentinelResource} 注解，可以通过以下两种方式解决：
 *  (1).sentinel定义的resource一定不能添加 / 的前缀，
 *  否则 {@link AbstractSentinelInterceptor} 会拦截一次， {@link SentinelResourceAspect} 切面也会拦截一次。
 *  (2).使用 `spring.cloud.sentinel.filter.enabled` 关闭拦截器。
 *  但是此时会有另外一个问题，注解的方式不能自定义 `limitApp` 属性，目前不支持解析。
 *  所以如果自定义了 `limitApp` 属性，只能是用web环境下的拦截器去进行处理。
 *  </li>
 *  <li>3.配置seata， 在nacos配置中心创建一个dataId为 service.vgroupMapping.account_service_group 的配置，
 *  GROUP为 SEATA_GROUP， 或者在当前应用配置文件添加 seata.service.vgroupMapping.account_service_group=default。
 *  <code>default</code> 的值为seata-server端在nacos注册中心的集群名称。
 *  启动seata：sh seata-server.sh -p 8091 -h 127.0.0.1 -m db</li>
 * </p>
 *
 * 配置中心加载bootstrap文件： {@link BootstrapApplicationListener}，监听 {@link ApplicationEnvironmentPreparedEvent} 事件，
 * 该事件由监听器 {@link EventPublishingRunListener} 发布，该监听器实现 {@link SpringApplicationRunListener} 接口，
 * 该接口为springboot应用启动阶段准备环境的时候回调。
 *
 * @author tianqingzhao
 * @since 2021/2/23 10:26
 */
@SpringBootApplication
@EnableScheduling
//@SpringBootApplication(exclude = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class})
//@EnableBinding({Sink.class})//接收消息
public class AccountServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(AccountServiceApplication.class, args);
    }
}