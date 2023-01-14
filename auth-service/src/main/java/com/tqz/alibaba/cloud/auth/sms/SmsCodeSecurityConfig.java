package com.tqz.alibaba.cloud.auth.sms;

import com.tqz.alibaba.cloud.auth.config.WebSecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.stereotype.Component;

/**
 * <p>注入 {@link AuthenticationProvider} 的实现类 {@link SmsCodeAuthenticationProvider}，
 * 否则 {@link ProviderManager} 无法选择 {@link SmsCodeAuthenticationProvider}
 *
 * @author tianqingzhao
 * @since 2023/1/10 8:33
 */
@Component
public class SmsCodeSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    
    @Autowired
    private SmsCodeAuthenticationProvider smsCodeAuthenticationProvider;
    
    /**
     * 短信验证码配置器。
     *
     * <p>所有的配置都可以移步到WebSecurityConfig ，
     * builder.authenticationProvider() 相当于 auth.authenticationProvider() ， 使用外部配置必须 要在 {@link
     * WebSecurityConfig#configure(HttpSecurity)} 中用 `http.apply(smsCodeSecurityConfig);` 将配置注入进去
     */
    @Override
    public void configure(HttpSecurity builder) throws Exception {
        builder.authenticationProvider(smsCodeAuthenticationProvider);
    }
}
