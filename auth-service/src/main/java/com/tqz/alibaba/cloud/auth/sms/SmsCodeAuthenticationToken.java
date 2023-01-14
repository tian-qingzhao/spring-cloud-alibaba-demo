package com.tqz.alibaba.cloud.auth.sms;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * <p>实现手机号验证码登录，参考 {@link UsernamePasswordAuthenticationToken}
 *
 * @author tianqingzhao
 * @since 2023/1/9 17:06
 */
public class SmsCodeAuthenticationToken extends AbstractAuthenticationToken {
    
    /**
     * 账号主体信息，表示手机号
     */
    private final Object principal;
    
    /**
     * 构建未授权的
     */
    public SmsCodeAuthenticationToken(Object principal) {
        super(null);
        this.principal = principal;
        setAuthenticated(false);
    }
    
    /**
     * 构建已授权的
     */
    public SmsCodeAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        super.setAuthenticated(true);
    }
    
    @Override
    public Object getCredentials() {
        return null;
    }
    
    @Override
    public Object getPrincipal() {
        return principal;
    }
    
    @Override
    public void setAuthenticated(boolean authenticated) {
        if (authenticated) {
            throw new IllegalArgumentException(
                    "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        }
        
        super.setAuthenticated(authenticated);
    }
    
    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
    }
}
