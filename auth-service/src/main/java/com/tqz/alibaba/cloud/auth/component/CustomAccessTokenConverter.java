package com.tqz.alibaba.cloud.auth.component;

import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;

/**
 * <p>自定义Token解析
 *
 * @author tianqingzhao
 * @since 2022/8/12 9:19
 */
public class CustomAccessTokenConverter extends DefaultAccessTokenConverter {
    
    public CustomAccessTokenConverter() {
        super.setUserTokenConverter(new CustomUserAuthenticationConverter());
    }
}
