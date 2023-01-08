package com.tqz.alibaba.cloud.gateway.config.rest;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerAutoConfiguration;
import org.springframework.cloud.client.loadbalancer.LoadBalancerInterceptor;
import org.springframework.cloud.client.loadbalancer.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;

import java.util.List;

/**
 * <p>自定义 RestTemplate 实现负载均衡配置类
 *
 * @author tianqingzhao
 * @since 2022/11/5 11:25
 */
@Configuration
@AutoConfigureAfter(LoadBalancerAutoConfiguration.class)
public class CustomRestTemplateConfig {
    
    /**
     * 自定义 RestTemplate beanName
     */
    public static final String CUSTOM_REST_TEMPLATE_NAME = "customRestTemplate";
    
    @Bean(name = CUSTOM_REST_TEMPLATE_NAME)
    public CustomRestTemplate customRestTemplate(DiscoveryClient discoveryClient) {
        return new CustomRestTemplate(discoveryClient);
    }
    
    @Bean
    public InitializingBean loadBalancerCustomRestTemplate(
            @Qualifier(CUSTOM_REST_TEMPLATE_NAME) CustomRestTemplate customRestTemplate,
            ObjectProvider<List<RestTemplateCustomizer>> restTemplateCustomizers) {
        return () -> {
            restTemplateCustomizers.ifAvailable(customizers -> {
                for (RestTemplateCustomizer customizer : customizers) {
                    customizer.customize(customRestTemplate);
                }
            });
        };
    }
    
    @Bean
    public RestTemplateCustomizer customRestTemplateCustomizer(LoadBalancerInterceptor loadBalancerInterceptor) {
        return restTemplate -> {
            List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
            interceptors.add(loadBalancerInterceptor);
            restTemplate.setInterceptors(interceptors);
        };
    }
    
    /*@Bean
    public RestTemplate myRestTemplate(LoadBalancerClient loadBalancer){
        RestTemplate restTemplate = new RestTemplate();
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        // ribbon核心拦截器
        interceptors.add(new LoadBalancerInterceptor(loadBalancer));
        restTemplate.setInterceptors(interceptors);
        return restTemplate;
    }*/
}
