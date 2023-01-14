package com.tqz.alibaba.cloud.common.base;

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
     * ROLE前綴
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
}
