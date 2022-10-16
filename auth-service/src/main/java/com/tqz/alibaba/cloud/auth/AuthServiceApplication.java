package com.tqz.alibaba.cloud.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

/**
 * <p>
 * Oauth2安全校验模块启动类
 * </p>
 *
 * @author tianqingzhao
 * @since 2021/3/3 17:32
 */
@SpringBootApplication
@EnableResourceServer
public class AuthServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
