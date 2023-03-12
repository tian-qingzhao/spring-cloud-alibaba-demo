package com.tqz.alibaba.cloud.auth.config;

import com.tqz.alibaba.cloud.auth.error.CustomAuthenticationEntryPoint;
import com.tqz.alibaba.cloud.auth.error.CustomClientCredentialsTokenEndpointFilter;
import com.tqz.alibaba.cloud.auth.error.CustomWebResponseExceptionTranslator;
import com.tqz.alibaba.cloud.auth.security.CustomJwtAccessTokenConverter;
import com.tqz.alibaba.cloud.auth.service.impl.UserDetailServiceImpl;
import com.tqz.alibaba.cloud.common.base.Constant;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.client.InMemoryClientDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import javax.sql.DataSource;

/**
 * <p>
 * 授权/认证服务器配置
 * </p>
 *
 * @author tianqingzhao
 * @since 2021/3/6 15:21
 */
@Configuration
@EnableAuthorizationServer
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
    
    @Autowired
    private UserDetailServiceImpl userDetailService;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private TokenGranter tokenGranter;
    
    /**
     * JwtAccessTokenConverter， TokenEnhancer的子类，帮助程序在JWT编码的令牌值和OAuth身份验证信息之间进行转换。
     */
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        //        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        
        // 自定义实现 JWT token 增强，也就是添加自己额外的信息，在资源服务器可以获取到结合业务场景去使用
        JwtAccessTokenConverter converter = new CustomJwtAccessTokenConverter();
        // 设置对称签名
        converter.setSigningKey(Constant.SIGNING_KEY);
        return converter;
    }
    
    /**
     * access_token存储器， 这里存储在数据库，大家可以结合自己的业务场景考虑将access_token存入数据库还是redis
     */
    @Bean
    public TokenStore tokenStore() {
        //        return new JdbcTokenStore(dataSource);  // 数据库存储
        return new JwtTokenStore(jwtAccessTokenConverter());
    }
    
    /**
     * jwt token的默认有效期为12小时，refresh token的有效期为30天， 如果要修改默认时间可以注入 DefaultTokenServices 并修改有效时间。
     */
    @Primary
    @Bean
    public DefaultTokenServices tokenServices() {
        DefaultTokenServices tokenServices = new DefaultTokenServices();
        tokenServices.setTokenEnhancer(jwtAccessTokenConverter());
        tokenServices.setTokenStore(tokenStore());
        tokenServices.setSupportRefreshToken(true);
        // 设置token有效期，默认12小时，此处修改为6小时   21600
        tokenServices.setAccessTokenValiditySeconds(60 * 60 * 6);
        // 设置refresh_token的有效期，默认30天，此处修改为7天
        tokenServices.setRefreshTokenValiditySeconds(60 * 60 * 24 * 7);
        return tokenServices;
    }
    
    /**
     * 从数据库读取clientDetails相关配置， 有 {@link InMemoryClientDetailsService} 和 {@link JdbcClientDetailsService} 两种方式选择
     */
    @Bean
    public ClientDetailsService clientDetails() {
        return new JdbcClientDetailsService(dataSource);
    }
    
    /**
     * 注入密码加密实现器
     */
    /*@Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }*/

    /**
     * 认证服务器Endpoints配置
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        // 如果需要使用refresh_token模式则需要注入userDetailService
        endpoints.userDetailsService(userDetailService);
        endpoints.authenticationManager(this.authenticationManager);
        endpoints.tokenStore(tokenStore());
        // endpoints.tokenEnhancer(jwtAccessTokenConverter()); // 下面tokenServices() 返回的bean里面配置了 jwtAccessTokenConverter()
        // endpoints.tokenServices(tokenServices());
        
        // 在 SmsCodeTokenGranterConfig 中已经创建了 AuthorizationServerTokenServices，
        // 所以我们可以将上面的 tokenServices 功能删除
        endpoints.tokenGranter(tokenGranter);
        
        endpoints.exceptionTranslator(new CustomWebResponseExceptionTranslator());
    }
    
    /**
     * 认证服务器相关接口权限管理
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        // 自定义认证配置的过滤器
        CustomClientCredentialsTokenEndpointFilter endpointFilter = new CustomClientCredentialsTokenEndpointFilter(
                security);
        endpointFilter.afterPropertiesSet();
        endpointFilter.setAuthenticationEntryPoint(new CustomAuthenticationEntryPoint());
        security.addTokenEndpointAuthenticationFilter(endpointFilter);
        
        security
                //.allowFormAuthenticationForClients() // 如果使用表单认证则需要加上 // 需要删除，否则上面自定义的过滤器不生效
                .tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
    }
    
    /**
     * client存储方式，此处使用jdbc存储
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(clientDetails());
    }
}
