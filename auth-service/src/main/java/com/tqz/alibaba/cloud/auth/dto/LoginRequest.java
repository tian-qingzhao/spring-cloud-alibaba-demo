package com.tqz.alibaba.cloud.auth.dto;

import lombok.Data;

/**
 * <p>自定义登录参数
 *
 * @author tianqingzhao
 * @since 2023/3/11 17:41
 */
@Data
public class LoginRequest {

    private String clientId;

    private String clientSecret;

    private String userName;

    private String password;

    private String grantType;

    private String mobile;

    private String smsCode;
}
