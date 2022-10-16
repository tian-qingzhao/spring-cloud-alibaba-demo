package com.tqz.alibaba.cloud.auth.security;

import com.tqz.alibaba.cloud.auth.user.SecurityUser;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>自定义Token增强
 *
 * @author tianqingzhao
 * @since 2022/8/11 15:15
 */
public class CustomJwtTokenConverter extends JwtAccessTokenConverter {
    
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication authentication) {
        SecurityUser securityUser = (SecurityUser) authentication.getUserAuthentication().getPrincipal();
        final Map<String, Object> additionalInformation = new HashMap<>(3);
        additionalInformation.put("userId", securityUser.getId());
        additionalInformation.put("mobile", securityUser.getMobile());
        additionalInformation.put("author", "tianqingzhao");
        ((DefaultOAuth2AccessToken) oAuth2AccessToken).setAdditionalInformation(additionalInformation);
        return super.enhance(oAuth2AccessToken, authentication);
    }
}
