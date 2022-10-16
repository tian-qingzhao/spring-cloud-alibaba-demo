package com.tqz.alibaba.cloud.gateway.route;

import com.tqz.alibaba.cloud.gateway.route.nacos.NacosConfigListenerRoute;
import com.tqz.alibaba.cloud.gateway.route.nacos.GatewayNacosProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>网关动态路由自动装配类
 *
 * @author tianqingzhao
 * @since 2022/8/28 12:29
 */
@Configuration
@EnableConfigurationProperties(GatewayNacosProperties.class)
public class GatewayDynamicRouteAutoConfiguration {
    
    @Bean
    public GatewayRouteRefresher gatewayDynamicRoute(RouteDefinitionWriter routeDefinitionWriter) {
        return new GatewayRouteRefresher(routeDefinitionWriter);
    }
    
    @Bean
    @ConditionalOnProperty(prefix = GatewayNacosProperties.PREFIX, name = "data-id")
    public NacosConfigListenerRoute nacosConfigListenerRoute(GatewayNacosProperties gatewayNacosProperties,
            GatewayRouteRefresher gatewayDynamicRoute) {
        return new NacosConfigListenerRoute(gatewayNacosProperties, gatewayDynamicRoute);
    }
}
