package com.tqz.alibaba.cloud.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.client.RestTemplate;

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
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> forEntity = restTemplate.getForEntity(
                "http://192.168.56.1:8848/nacos/v1/cs/configs?group=DEFAULT_GROUP&dataId=mysql-example&username=nacos&password=nacos",
                String.class);
        System.out.println(forEntity.getBody());
    
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
