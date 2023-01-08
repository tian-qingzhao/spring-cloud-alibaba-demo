package com.tqz.alibaba.cloud.account.config;

import com.tqz.alibaba.cloud.common.handler.CustomAccessDeniedHandler;
import com.tqz.alibaba.cloud.common.handler.CustomAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;


/**
 * <p>
 * 资源服务器配置
 * </p>
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
        http
                .authorizeRequests()
                .requestMatchers(EndpointRequest.toAnyEndpoint()).permitAll()
                .antMatchers(
                    "/v2/api-docs/**",
                    "/swagger-resources/**",
                    "/swagger-ui.html",
                    "/webjars/**"
                    ).permitAll()
                .anyRequest().authenticated()
                .and()
                //统一自定义异常
                .exceptionHandling()
                .accessDeniedHandler(new CustomAccessDeniedHandler())
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                .and()
                .csrf().disable();
    }

    /*@Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        DefaultAccessTokenConverter accessTokenConverter = new DefaultAccessTokenConverter();
        UserAuthenticationConverter userTokenConverter = new CustomUserAuthenticationConverter();
        accessTokenConverter.setUserTokenConverter(userTokenConverter);

        resources.resourceId(resourceId)
                .tokenStore(tokenStore());
    }

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(jwtTokenConverter());
    }


    @Bean
    public JwtAccessTokenConverter jwtTokenConverter(){
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        jwtAccessTokenConverter.setSigningKey("javadaily");
        jwtAccessTokenConverter.setAccessTokenConverter(new CustomAccessTokenConverter());

        return jwtAccessTokenConverter;
    }*/

//    @Bean
//    public UserAuthenticationConverter userAuthenticationConverter(){
//        return new CustomTokenConverter();
//    }
}
