package com.tqz.alibaba.cloud.auth.sms;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p>继承 {@link AbstractTokenGranter} 扩展认证模式 sms_code ，需要将其添加到Spring中并通过grantType被选中。
 *
 * @author tianqingzhao
 * @since 2023/1/10 8:44
 */
@Slf4j
public class SmsCodeTokenGranter extends AbstractTokenGranter {
    
    private static final String GRANT_TYPE = "sms_code";
    
    private final AuthenticationManager authenticationManager;
    
    public SmsCodeTokenGranter(AuthenticationManager authenticationManager,
            AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService,
            OAuth2RequestFactory requestFactory) {
        this(authenticationManager, tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
    }
    
    public SmsCodeTokenGranter(AuthenticationManager authenticationManager,
            AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService,
            OAuth2RequestFactory requestFactory, String grantType) {
        super(tokenServices, clientDetailsService, requestFactory, grantType);
        this.authenticationManager = authenticationManager;
    }
    
    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
        log.info("执行SmsCodeTokenGranter#getOAuth2Authentication()");
        
        Map<String, String> parameters = new LinkedHashMap<>(tokenRequest.getRequestParameters());
        String mobile = parameters.get("mobile");
        
        Authentication userAuth = new SmsCodeAuthenticationToken(mobile);
        
        ((AbstractAuthenticationToken) userAuth).setDetails(parameters);
        
        try {
            log.info("SmsCodeTokenGranter#getOAuth2Authentication() 开始认证");
            userAuth = this.authenticationManager.authenticate(userAuth);
            logger.info("SmsCodeTokenGranter#getOAuth2Authentication() 认证通过");
        } catch (AccountStatusException | BadCredentialsException ex) {
            log.error("SmsCodeTokenGranter#getOAuth2Authentication() 认证失败");
            ex.printStackTrace();
            throw new InvalidGrantException(ex.getMessage());
        }
        
        if (userAuth != null && userAuth.isAuthenticated()) {
            OAuth2Request request = this.getRequestFactory().createOAuth2Request(client, tokenRequest);
            return new OAuth2Authentication(request, userAuth);
        } else {
            log.error("手机号 {} 认证失败.", mobile);
            throw new InvalidGrantException("Could not authenticate mobile: " + mobile);
        }
    }
}
