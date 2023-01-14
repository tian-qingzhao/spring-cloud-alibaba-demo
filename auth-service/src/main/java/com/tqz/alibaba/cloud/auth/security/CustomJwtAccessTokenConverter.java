package com.tqz.alibaba.cloud.auth.security;

import com.tqz.alibaba.cloud.common.base.Constant;
import com.tqz.alibaba.cloud.common.security.SecurityUser;
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
public class CustomJwtAccessTokenConverter extends JwtAccessTokenConverter {
    
    /**
     * 作者默认名
     */
    private static final String AUTHOR_NAME = "tianqingzhao";
    
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication authentication) {
        SecurityUser securityUser = (SecurityUser) authentication.getUserAuthentication().getPrincipal();
        final Map<String, Object> additionalInformation = new HashMap<>(3);
        additionalInformation.put(Constant.USER_ID_KEY, securityUser.getId());
        additionalInformation.put(Constant.MOBILE_KEY, securityUser.getMobile());
        additionalInformation.put(Constant.AUTHOR_KEY, AUTHOR_NAME);
        ((DefaultOAuth2AccessToken) oAuth2AccessToken).setAdditionalInformation(additionalInformation);
        return super.enhance(oAuth2AccessToken, authentication);
    }
}
