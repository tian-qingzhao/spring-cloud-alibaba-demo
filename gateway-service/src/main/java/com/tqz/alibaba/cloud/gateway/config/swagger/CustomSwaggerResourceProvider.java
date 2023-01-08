package com.tqz.alibaba.cloud.gateway.config.swagger;

import lombok.AllArgsConstructor;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.support.NameUtils;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 自定义Swagger资源,聚合其他服务的接口api
 * </p>
 *
 * @author tianqingzhao
 * @since 2021/3/1 17:48
 */
@Component("customSwaggerResourceProvider")
@AllArgsConstructor
public class CustomSwaggerResourceProvider implements SwaggerResourcesProvider {
    
    /**
     * Swagger2默认的url后缀
     */
    public static final String SWAGGER2URL = "/v2/api-docs";
    
    /**
     * 网关路由
     */
    private final RouteLocator routeLocator;
    
    private final GatewayProperties gatewayProperties;
    
    /**
     * 聚合其他服务接口
     *
     * @return
     */
    @Override
    public List<SwaggerResource> get() {
        List<SwaggerResource> resourceList = new ArrayList<>();
        List<String> routes = new ArrayList<>();
        // 获取网关中配置的route
        routeLocator.getRoutes().subscribe(route -> routes.add(route.getId()));
        
        gatewayProperties.getRoutes()
                .stream()
                .filter(routeDefinition -> routes.contains(routeDefinition.getId()))
                .forEach(routeDefinition -> routeDefinition.getPredicates().stream()
                        .filter(predicateDefinition -> "Path".equalsIgnoreCase(predicateDefinition.getName()))
                        .forEach(predicateDefinition -> resourceList.add(
                                        swaggerResource(routeDefinition.getId(), getLocation(predicateDefinition)))
                        )
                );
        return resourceList;
    }
    
    private SwaggerResource swaggerResource(String name, String location) {
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setLocation(location);
        swaggerResource.setSwaggerVersion("2.0");
        return swaggerResource;
    }
    
    /**
     * 这里拼接时需要注意,网关配置account映射到account-service， 要么将网关的配置修改成account-service映射成account-service，
     * 要么就需要像我这一样，由于我们命名规则一直，直接将/**替换成 account-service/v2/api-docs
     *
     * @param predicateDefinition
     * @return
     */
    private String getLocation(PredicateDefinition predicateDefinition) {
        return predicateDefinition.getArgs().get(NameUtils.GENERATED_NAME_PREFIX + "0").replace("/**", SWAGGER2URL);
    }
}
