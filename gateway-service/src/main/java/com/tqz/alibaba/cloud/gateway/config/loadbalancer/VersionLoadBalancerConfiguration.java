package com.tqz.alibaba.cloud.gateway.config.loadbalancer;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * 自定义负载均衡器配置实现类第一种方式，
 * 该类不需要添加 {@link org.springframework.context.annotation.Configuration} 注解。
 *
 * @author tianqingzhao
 * @since 2023/3/9 14:48
 */
public class VersionLoadBalancerConfiguration {

    @Bean
    ReactorLoadBalancer<ServiceInstance> versionGrayLoadBalancer(Environment environment,
                                                                 LoadBalancerClientFactory loadBalancerClientFactory) {
        String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
        return new VersionLoadBalancer(
                loadBalancerClientFactory.getLazyProvider(name, ServiceInstanceListSupplier.class), name);
    }

}