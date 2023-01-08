package com.tqz.alibaba.cloud.common.base;

/**
 * <p>
 * 公共返回状态码
 * </p>
 *
 * @author tianqingzhao
 * @since 2021/2/25 11:15
 */
public enum ReturnCode {
    
    /**
     * 成功
     */
    RC100(200, "请求成功"),
    
    /**
     * 服务限流
     */
    RC201(201, "服务开启限流保护,请稍后再试!"),
    
    /**
     * 认证无权限
     */
    RC401(401, "匿名用户访问无权限资源时的异常"),
    
    /**
     * 匿名无权限
     */
    RC403(403, "认证用户访问无权限资源时的异常"),
    
    /**
     * 服务异常
     */
    RC999(500, "操作失败");
    
    /**
     * 自定义状态码
     */
    private int code;
    
    /**
     * 自定义描述
     */
    private String message;
    
    private ReturnCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public int getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
}
