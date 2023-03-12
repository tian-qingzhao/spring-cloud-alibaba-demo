package com.tqz.alibaba.cloud.common.base;

import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * <p>
 * 系统公共常量
 * </p>
 *
 * @author tianqingzhao
 * @since 2021/2/25 11:15
 */
public class Constant {
    
    /**
     * 有效状态
     */
    public static final String VALID_STATUS = "VALID";
    
    /**
     * 无效状态
     */
    public static final String INVALID_STATUS = "INVALID";
    
    /**
     * ROLE前綴。
     *
     * <p>Spring Security 基于微服务授权，也就是基于方法授权，使用 {@link PreAuthorize} 注解去实现，
     * 该注解的实现在 {@link SecurityExpressionRoot} 抽象类，该类的属性 `defaultRolePrefix` 默认为 `ROLE_`，
     * 也就是使用该注解默认判断角色权限的前缀看看是否包含 `ROLE_` ，如果没有包含该前缀，会匹配失败。
     */
    public static final String ROLE_PREFIX = "ROLE_";
    
    /**
     * account-service服务名称
     */
    public static final String ACCOUNT_SERVICE_NAME = "account-service";
    
    /**
     * product-service服务名称
     */
    public static final String PRODUCT_SERVICE_NAME = "product-service";
    
    /**
     * order-service服务名称
     */
    public static final String ORDER_SERVICE_NAME = "order-service";
    
    /**
     * 对称签名的秘钥，认证服务器(auth)和资源服务器(account/product/order)需要使用相同的秘钥
     */
    public static final String SIGNING_KEY = "spring-cloud-alibaba-demo";
    
    /**
     * 用户名的key
     */
    public static final String USER_ID_KEY = "userId";
    
    /**
     * 手机号的key
     */
    public static final String MOBILE_KEY = "mobile";
    
    /**
     * 作者的key
     */
    public static final String AUTHOR_KEY = "author";
    
    /**
     * 中括号前缀
     */
    public static final String BRACKETS_PREFIX = "[";
    
    /**
     * 中括号后缀
     */
    public static final String BRACKETS_SUFFIX = "]";

    /**
     * JWT请求头认证的key
     */
    public static final String JWT_AUTHORIZATION_HEADER_KEY = "Authorization";

    /**
     * 无效Token在redis中的前缀
     */
    public static final String REDIS_TOKEN_BLACKLIST_PREFIX = "InvalidToken";
}
