package com.tqz.alibaba.cloud.product.config;//package com.tqz.product.config;

import com.tqz.alibaba.cloud.common.handler.CustomAccessDeniedHandler;
import com.tqz.alibaba.cloud.common.handler.CustomAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

/**
 * <p>资源服务器配置
 *
 * @author tianqingzhao
 * @since 2022/8/11 15:32
 */
//@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
    
    @Value("${security.oauth2.resource.id}")
    private String resourceId;
    
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().requestMatchers(EndpointRequest.toAnyEndpoint()).permitAll()
                .antMatchers("/v2/api-docs/**", "/swagger-resources/**", "/swagger-ui.html", "/webjars/**").permitAll()
                .anyRequest().authenticated().and()
                //统一自定义异常
                .exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                .accessDeniedHandler(new CustomAccessDeniedHandler()).and().csrf().disable();
    }
    
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId(resourceId).tokenStore(tokenStore());
    }
    
    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(jwtTokenEnhancer());
    }
    
    @Bean
    public JwtAccessTokenConverter jwtTokenEnhancer() {
        JwtAccessTokenConverter jwtTokenEnhancer = new JwtAccessTokenConverter();
        jwtTokenEnhancer.setSigningKey("javadaily");
        return jwtTokenEnhancer;
    }
}
