package com.tqz.alibaba.cloud.gateway.config.loadbalancer;

import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * 自定义负载均衡器配置实现类第二种实现方式，
 * 该类不需要添加 {@link org.springframework.context.annotation.Configuration} 注解。
 *
 * @author tianqingzhao
 * @since 2023/3/10 17:09
 */
public class VersionServiceInstanceListSupplierConfiguration {

    @Bean
    ServiceInstanceListSupplier serviceInstanceListSupplier(ConfigurableApplicationContext context) {
        ServiceInstanceListSupplier delegate = ServiceInstanceListSupplier.builder()
                .withDiscoveryClient()
                .withCaching()
                .build(context);
        return new VersionServiceInstanceListSupplier(delegate);
    }
}