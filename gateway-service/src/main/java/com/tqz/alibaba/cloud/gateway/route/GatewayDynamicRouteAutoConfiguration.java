package com.tqz.alibaba.cloud.gateway.route;

import com.tqz.alibaba.cloud.gateway.route.nacos.GatewayNacosProperties;
import com.tqz.alibaba.cloud.gateway.route.nacos.NacosConfigListenerRoute;
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
    public GatewayRouteRefresher gatewayRouteRefresher(RouteDefinitionWriter routeDefinitionWriter) {
        return new GatewayRouteRefresher(routeDefinitionWriter);
    }
    
    @Bean
    public NacosConfigListenerRoute nacosConfigListenerRoute(GatewayNacosProperties gatewayNacosProperties,
            GatewayRouteRefresher gatewayRouteRefresher) {
        return new NacosConfigListenerRoute(gatewayNacosProperties, gatewayRouteRefresher);
    }
}
