package com.tqz.alibaba.cloud.auth.sms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenGranter;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeTokenGranter;
import org.springframework.security.oauth2.provider.code.InMemoryAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.implicit.ImplicitTokenGranter;
import org.springframework.security.oauth2.provider.password.ResourceOwnerPasswordTokenGranter;
import org.springframework.security.oauth2.provider.refresh.RefreshTokenGranter;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>将我们的短信认证模式添加到Spring中，
 * 参考 {@link AuthorizationServerEndpointsConfigurer#getDefaultTokenGranters()} 方法实现。
 *
 * @author tianqingzhao
 * @since 2023/1/10 8:50
 */
@Configuration
public class SmsCodeTokenGranterConfig {
    
    @Autowired
    private ClientDetailsService clientDetailsService;
    
    private TokenGranter tokenGranter;
    
    @Autowired
    private TokenStore tokenStore;
    
    @Autowired
    TokenEnhancer tokenEnhancer;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    private AuthorizationServerTokenServices tokenServices;
    
    private boolean reuseRefreshToken = true;
    
    private AuthorizationCodeServices authorizationCodeServices;
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Bean
    public TokenGranter tokenGranter() {
        if (null == tokenGranter) {
            tokenGranter = new TokenGranter() {
                private CompositeTokenGranter delegate;
                
                @Override
                public OAuth2AccessToken grant(String grantType, TokenRequest tokenRequest) {
                    if (delegate == null) {
                        delegate = new CompositeTokenGranter(getDefaultTokenGranters());
                    }
                    return delegate.grant(grantType, tokenRequest);
                }
            };
        }
        return tokenGranter;
    }
    
    private List<TokenGranter> getDefaultTokenGranters() {
        AuthorizationServerTokenServices tokenServices = tokenServices();
        AuthorizationCodeServices authorizationCodeServices = authorizationCodeServices();
        OAuth2RequestFactory requestFactory = requestFactory();
        
        List<TokenGranter> tokenGranters = new ArrayList<>();
        // 授权码模式
        tokenGranters.add(
                new AuthorizationCodeTokenGranter(tokenServices, authorizationCodeServices, clientDetailsService,
                        requestFactory));
        // refresh模式
        tokenGranters.add(new RefreshTokenGranter(tokenServices, clientDetailsService, requestFactory));
        // 简化模式
        ImplicitTokenGranter implicit = new ImplicitTokenGranter(tokenServices, clientDetailsService, requestFactory);
        tokenGranters.add(implicit);
        // 客户端模式
        tokenGranters.add(new ClientCredentialsTokenGranter(tokenServices, clientDetailsService, requestFactory));
        
        if (authenticationManager != null) {
            // 密码模式
            tokenGranters.add(
                    new ResourceOwnerPasswordTokenGranter(authenticationManager, tokenServices, clientDetailsService,
                            requestFactory));
            // 添加自定义的短信验证码模式
            tokenGranters.add(new SmsCodeTokenGranter(authenticationManager, tokenServices, clientDetailsService,
                    requestFactory));
        }
        
        return tokenGranters;
    }
    
    private AuthorizationServerTokenServices tokenServices() {
        if (tokenServices != null) {
            return tokenServices;
        }
        this.tokenServices = createDefaultTokenServices();
        return tokenServices;
    }
    
    private AuthorizationServerTokenServices createDefaultTokenServices() {
        DefaultTokenServices tokenServices = new DefaultTokenServices();
        tokenServices.setTokenStore(tokenStore);
        tokenServices.setSupportRefreshToken(true);
        tokenServices.setReuseRefreshToken(reuseRefreshToken);
        tokenServices.setClientDetailsService(clientDetailsService);
        tokenServices.setTokenEnhancer(tokenEnhancer);
        addUserDetailsService(tokenServices, this.userDetailsService);
        return tokenServices;
    }
    
    /**
     * 添加预身份验证
     *
     * @param tokenServices
     * @param userDetailsService
     */
    private void addUserDetailsService(DefaultTokenServices tokenServices, UserDetailsService userDetailsService) {
        if (userDetailsService != null) {
            PreAuthenticatedAuthenticationProvider provider = new PreAuthenticatedAuthenticationProvider();
            provider.setPreAuthenticatedUserDetailsService(
                    new UserDetailsByNameServiceWrapper<PreAuthenticatedAuthenticationToken>(userDetailsService));
            tokenServices.setAuthenticationManager(
                    new ProviderManager(Arrays.<AuthenticationProvider>asList(provider)));
        }
    }
    
    /**
     * OAuth2RequestFactory的默认实现，它初始化参数映射中的字段， 验证授权类型(grant_type)和范围(scope)，并使用客户端的默认值填充范围(scope)（如果缺少这些值）。
     */
    private OAuth2RequestFactory requestFactory() {
        return new DefaultOAuth2RequestFactory(clientDetailsService);
    }
    
    /**
     * 授权码API
     *
     * @return
     */
    private AuthorizationCodeServices authorizationCodeServices() {
        if (this.authorizationCodeServices == null) {
            this.authorizationCodeServices = new InMemoryAuthorizationCodeServices();
        }
        return this.authorizationCodeServices;
    }
}