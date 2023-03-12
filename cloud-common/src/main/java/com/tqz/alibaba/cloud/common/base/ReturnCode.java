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
    RC500(500, "操作失败"),

    /**
     * OAuth2客户端认证失败
     */
    CLIENT_AUTHENTICATION_FAILED(1001, "客户端认证失败，client_id或client_secret错误"),

    /**
     * OAuth2不支持的认证模式
     */
    UNSUPPORTED_GRANT_TYPE(1003, "不支持的认证模式"),

    /**
     * OAuth2用户名或密码错误
     */
    USERNAME_OR_PASSWORD_ERROR(1002, "用户名或密码错误"),

    /**
     * 访问令牌不合法
     */
    INVALID_TOKEN(2001, "访问令牌不合法"),

    /**
     * 该令牌已过期，请重新获取令牌
     */
    INVALID_TOKEN_OR_EXPIRED(2004, "该令牌已过期，请重新获取令牌");

    /**
     * 自定义状态码
     */
    private final int code;

    /**
     * 自定义描述
     */
    private final String message;

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
