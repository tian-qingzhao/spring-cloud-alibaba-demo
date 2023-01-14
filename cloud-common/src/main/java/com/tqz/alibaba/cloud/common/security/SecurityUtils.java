package com.tqz.alibaba.cloud.common.security;

import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * <p>oauth2 工具类
 *
 * @author tianqingzhao
 * @since 2022/8/12 9:18
 */
@UtilityClass
public class SecurityUtils {
    
    /**
     * 获取Authentication
     */
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
    
    public SecurityUser getUser() {
        Authentication authentication = getAuthentication();
        if (authentication == null) {
            return null;
        }
        return getUser(authentication);
    }
    
    /**
     * 获取当前用户
     *
     * @param authentication 认证信息
     * @return 当前用户
     */
    private static SecurityUser getUser(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof SecurityUser) {
            return (SecurityUser) principal;
        }
        return null;
    }
}
