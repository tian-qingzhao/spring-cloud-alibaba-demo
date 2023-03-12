package com.tqz.alibaba.cloud.auth.error;

import com.tqz.alibaba.cloud.auth.config.AuthorizationServerConfig;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenEndpointFilter;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
 * 自定义OAuth2客户端认证异常的过滤器，也就是client_id、client_secret不正确的情况。
 *
 * <p>Spring Security OAuth2 官方默认使用的 {@link ClientCredentialsTokenEndpointFilter} ,这里继承该类，
 * 并在 {@link #afterPropertiesSet()} 初始化方法里面使用 {@link AuthenticationEntryPoint} 自定义的实现类。
 *
 * <p>最后需要把该类注入到 {@link AuthorizationServerConfig#configure(AuthorizationServerSecurityConfigurer)} 配置类里面。
 *
 * @author tianqingzhao
 * @since 2023/1/15 17:40
 */
public class CustomClientCredentialsTokenEndpointFilter extends ClientCredentialsTokenEndpointFilter {
    
    private final AuthorizationServerSecurityConfigurer configurer;
    
    private AuthenticationEntryPoint authenticationEntryPoint;
    
    public CustomClientCredentialsTokenEndpointFilter(AuthorizationServerSecurityConfigurer configurer) {
        this.configurer = configurer;
    }
    
    @Override
    public void afterPropertiesSet() {
        setAuthenticationFailureHandler(
                (request, response, e) -> authenticationEntryPoint.commence(request, response, e));
        setAuthenticationSuccessHandler((request, response, authentication) -> {
        });
    }
    
    @Override
    public void setAuthenticationEntryPoint(AuthenticationEntryPoint authenticationEntryPoint) {
        super.setAuthenticationEntryPoint(null);
        this.authenticationEntryPoint = authenticationEntryPoint;
    }
    
    @Override
    protected AuthenticationManager getAuthenticationManager() {
        return configurer.and().getSharedObject(AuthenticationManager.class);
    }
    
}
