package com.tqz.alibaba.cloud.auth.dto;

import java.util.Arrays;

/**
 * <p>登录类型，针对自定义登录接口使用，可继续添加其他类型处理。
 *
 * @author tianqingzhao
 * @since 2023/3/11 17:41
 */
public enum LoginTypeEnum {

    /**
     * 密码模式
     */
    PASSWORD("password"),
    /**
     * 短信验证码模式
     */
    SMSCODE("sms_code");

    private final String grantType;

    LoginTypeEnum(String grantType) {
        this.grantType = grantType;
    }

    public String getGrantType() {
        return grantType;
    }

    public static LoginTypeEnum fromGrantType(String grantType) {
        return Arrays.stream(LoginTypeEnum.values())
                .filter(item -> item.getGrantType().equals(grantType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("不支持此登录类型"));
    }
}
