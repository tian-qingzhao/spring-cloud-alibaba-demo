package com.tqz.alibaba.cloud.gateway.route.nacos;

import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.AbstractListener;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.tqz.alibaba.cloud.gateway.route.GatewayRouteRefresher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

/**
 * <p>nacos配置中心监听路由
 *
 * @author tianqingzhao
 * @since 2022/8/28 11:19
 */
@Slf4j
public class NacosConfigListenerRoute implements InitializingBean {
    
    private final GatewayNacosProperties gatewayNacosProperties;
    
    private final GatewayRouteRefresher gatewayRouteRefresher;
    
    private ConfigService configService;
    
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    
    public NacosConfigListenerRoute(GatewayNacosProperties gatewayNacosProperties,
            GatewayRouteRefresher gatewayRouteRefresher) {
        this.gatewayNacosProperties = gatewayNacosProperties;
        this.gatewayRouteRefresher = gatewayRouteRefresher;
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        if (!gatewayNacosProperties.isEnabled()) {
            log.info("网关动态路由关闭从Nacos配置中心获取数据");
            return;
        }
        
        initNacosConfigService();
        
        if (configService == null) {
            log.warn("`configService` 对象为空，不通过nacos配置中心获取动态网关路由");
            return;
        }
        
        String configStr = configService.getConfig(gatewayNacosProperties.getDataId(),
                gatewayNacosProperties.getGroup(), gatewayNacosProperties.getTimeoutMs());
        log.info("项目启动首次获取到网关路由配置：{}", configStr);
        if (configStr == null) {
            return;
        }
        
        List<RouteDefinition> routeDefinitionList = convert(configStr);
        if (CollectionUtils.isEmpty(routeDefinitionList)) {
            return;
        }
        
        routeDefinitionList.forEach(gatewayRouteRefresher::add);
        
        registerNacosListenerGetRoute();
    }
    
    /**
     * 注册nacos监听器获取路由配置，nacos配置中心通过 {@link Listener} 监听器实现配置刷新
     */
    private void registerNacosListenerGetRoute() {
        try {
            configService.addListener(gatewayNacosProperties.getDataId(), gatewayNacosProperties.getGroup(),
                    new AbstractListener() {
                        
                        @Override
                        public void receiveConfigInfo(String configInfo) {
                            log.info("Nacos监听器监听到网关路由配置变化：{}", configInfo);
                            List<RouteDefinition> routeDefinitionList = convert(configInfo);
                            
                            gatewayRouteRefresher.refreshAll(
                                    Optional.ofNullable(routeDefinitionList).orElseGet(ArrayList::new));
                        }
                    });
        } catch (NacosException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 获取 {@link ConfigService} 对象
     */
    private void initNacosConfigService() {
        Properties properties = new Properties();
        
        properties.setProperty(PropertyKeyConst.SERVER_ADDR, gatewayNacosProperties.getServerAddr());
        properties.setProperty(PropertyKeyConst.NAMESPACE, gatewayNacosProperties.getNamespace());
        properties.setProperty(PropertyKeyConst.USERNAME, Objects.toString(gatewayNacosProperties.getUsername(), ""));
        properties.setProperty(PropertyKeyConst.PASSWORD, Objects.toString(gatewayNacosProperties.getPassword(), ""));
        
        try {
            configService = ConfigFactory.createConfigService(properties);
        } catch (NacosException e) {
            log.error("获取 `ConfigService` 对象失败");
            e.printStackTrace();
        }
    }
    
    /**
     * 转换配置内容为字符串
     *
     * @param data 配置内容数据
     * @return {@link RouteDefinition} 路由集合
     */
    private <T> List<T> convert(String data) {
        try {
            CollectionType listType = OBJECT_MAPPER.getTypeFactory()
                    .constructCollectionType(ArrayList.class, RouteDefinition.class);
            return OBJECT_MAPPER.readValue(data, listType);
        } catch (JsonProcessingException e) {
            log.error("转换nacos配置中心网关路由失败");
            e.printStackTrace();
            return null;
        }
    }
}
