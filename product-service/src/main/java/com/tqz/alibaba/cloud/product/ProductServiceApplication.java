package com.tqz.alibaba.cloud.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * <p>
 * product-service启动类
 * </p>
 *
 * @author tianqingzhao
 * @since 2021/2/25 10:15
 */
@SpringBootApplication
@EnableSwagger2
public class ProductServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }
}
