package com.tqz.alibaba.cloud.gateway.route;

import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * <p>网关动态路由
 *
 * @author tianqingzhao
 * @since 2022/8/28 10:51
 */
public class GatewayRouteRefresher implements ApplicationEventPublisherAware {
    
    private RouteDefinitionWriter routeDefinitionWriter;
    
    private ApplicationEventPublisher publisher;
    
    public GatewayRouteRefresher(RouteDefinitionWriter routeDefinitionWriter) {
        this.routeDefinitionWriter = routeDefinitionWriter;
    }
    
    public void add(RouteDefinition routeDefinition) {
        routeDefinitionWriter.save(Mono.just(routeDefinition)).subscribe();
    }
    
    public void update(RouteDefinition routeDefinition) {
        routeDefinitionWriter.delete(Mono.just(routeDefinition.getId()));
        routeDefinitionWriter.save(Mono.just(routeDefinition)).subscribe();
    }
    
    public void refreshAll(List<RouteDefinition> routeDefinitions) {
        routeDefinitions.forEach(this::update);
        publisher.publishEvent(new RefreshRoutesEvent(this));
    }
    
    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }
    
    public static void main(String[] args) {
        System.out.println(Math.min(6000 << 6, 1000L * 60));
    }
}