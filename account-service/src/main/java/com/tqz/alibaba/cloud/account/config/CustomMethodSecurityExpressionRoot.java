package com.tqz.alibaba.cloud.account.config;

import com.tqz.alibaba.cloud.common.base.Constant;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.AntPathMatcher;

import java.util.Collection;

/**
 * 自定义方法级别权限校验。
 *
 * <p>使用 Spring Security {@link PreAuthorize} 基于微服务方法级别鉴权，实现为 {@link SecurityExpressionRoot} 抽象类，
 * 该类默认提供的几个方法：
 * <li>{@link SecurityExpressionRoot#hasAuthority(String)}</li>
 * <li>{@link SecurityExpressionRoot#hasAnyAuthority(String...)}</li>
 * <li>{@link SecurityExpressionRoot#hasRole(String)}</li>
 * <li>{@link SecurityExpressionRoot#hasAnyRole(String...)}</li>
 *
 * <p>这里继承 {@link SecurityExpressionRoot} 抽象类，自定义方法 {@link #hasPrivilege(String)} 实现角色权限的判断，
 * 然后方法上使用 {@link PreAuthorize} 注解就可以使用自定义方法了，例如：@PreAuthorize("hasPrivilege('queryAccount')") 。
 *
 * @author tianqingzhao
 * @since 2021/3/1 9:48
 */
public class CustomMethodSecurityExpressionRoot extends SecurityExpressionRoot
        implements MethodSecurityExpressionOperations {
    
    private Object filterObject;
    
    private Object returnObject;
    
    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();
    
    public CustomMethodSecurityExpressionRoot(Authentication authentication) {
        super(authentication);
    }
    
    public boolean hasPrivilege(String permission) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        return authorities.stream().map(GrantedAuthority::getAuthority)
                .filter(item -> !item.startsWith(Constant.ROLE_PREFIX))
                .anyMatch(x -> ANT_PATH_MATCHER.match(x, permission));
    }
    
    @Override
    public Object getThis() {
        return this;
    }
    
    @Override
    public Object getFilterObject() {
        return filterObject;
    }
    
    @Override
    public void setFilterObject(Object filterObject) {
        this.filterObject = filterObject;
    }
    
    @Override
    public Object getReturnObject() {
        return returnObject;
    }
    
    @Override
    public void setReturnObject(Object returnObject) {
        this.returnObject = returnObject;
    }
    
}