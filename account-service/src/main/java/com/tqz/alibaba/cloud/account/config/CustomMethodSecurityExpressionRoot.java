package com.tqz.alibaba.cloud.account.config;/*
package com.tqz.account.config;

import com.tqz.base.CloudConstant;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.AntPathMatcher;

import java.util.Collection;

*/
/**
 * <p>
 * 自定义权限校验
 * </p>
 *
 * @author tianqingzhao
 * @since 2021/3/1 9:48
 *//*
public class CustomMethodSecurityExpressionRoot extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {

    private static final AntPathMatcher antPathMatcher = new AntPathMatcher();

    public CustomMethodSecurityExpressionRoot(Authentication authentication) {
        super(authentication);
    }

    private Object filterObject;
    private Object returnObject;


    public boolean hasPrivilege(String permission){
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        return authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .filter(item -> !item.startsWith(CloudConstant.ROLE_PREFIX))
                    .anyMatch(x -> antPathMatcher.match(x, permission));
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
*/
