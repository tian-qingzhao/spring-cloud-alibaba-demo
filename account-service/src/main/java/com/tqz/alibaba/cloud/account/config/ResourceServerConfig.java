package com.tqz.alibaba.cloud.account.config;

import com.tqz.alibaba.cloud.common.base.Constant;
import com.tqz.alibaba.cloud.common.handler.CustomAccessDeniedHandler;
import com.tqz.alibaba.cloud.common.handler.CustomAuthenticationEntryPoint;
import com.tqz.alibaba.cloud.common.security.CustomAccessTokenConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationManager;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationProcessingFilter;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;


/**
 * <p>
 * 资源服务器配置
 *
 * <p>用了透明令牌jwt token后资源服务器可以直接解析验证token，不再需要调用认证服务器接口，
 * 配置文件去掉 `security.oauth2.resource.user-info-uri` 属性配置，使用jwt解析access_token并校验是否正确，
 * 给应用添加 {@link EnableResourceServer} 注解后会给 Spring Security 的 FilterChan 添加一个 {@link
 * OAuth2AuthenticationProcessingFilter}， OAuth2AuthenticationProcessingFilter 会使用 {@link OAuth2AuthenticationManager}
 * 来验证token。可在 {@link OAuth2AuthenticationProcessingFilter#doFilter} 处打个断点体会一下校验过程。
 * 校验逻辑如下：
 * 1. {@link OAuth2AuthenticationProcessingFilter#doFilter} 调用 `tokenExtractor.extract(request);` 获取认证信息，
 * 2.如果获取到认证信息，继续在 {@link OAuth2AuthenticationProcessingFilter#doFilter} 方法调用 `authenticationManager.authenticate(authentication);`
 * 3.第二布authenticate方法进入到 {@link OAuth2AuthenticationManager#authenticate(Authentication)} 的实现
 * 4. authenticat方法调用 `tokenServices.loadAuthentication(token);`
 * 5.第四部进入到 {@link DefaultTokenServices#loadAuthentication(String)} 的实现
 *
 * @author tianqingzhao
 * @since 2021/3/1 9:48
 */
@Configuration
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
                .exceptionHandling().accessDeniedHandler(new CustomAccessDeniedHandler())
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint()).and().csrf().disable();
    }
    
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        //        DefaultAccessTokenConverter accessTokenConverter = new DefaultAccessTokenConverter();
        //        UserAuthenticationConverter userTokenConverter = new CustomUserAuthenticationConverter();
        //        accessTokenConverter.setUserTokenConverter(userTokenConverter);
        
        resources.resourceId(resourceId).tokenStore(tokenStore());
    }
    
    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(jwtTokenConverter());
    }
    
    
    @Bean
    public JwtAccessTokenConverter jwtTokenConverter() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        jwtAccessTokenConverter.setSigningKey(Constant.SIGNING_KEY);
        jwtAccessTokenConverter.setAccessTokenConverter(new CustomAccessTokenConverter());
        
        return jwtAccessTokenConverter;
    }
    
    //    @Bean
    //    public UserAuthenticationConverter userAuthenticationConverter(){
    //        return new CustomTokenConverter();
    //    }
}
