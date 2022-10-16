package com.tqz.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <p>
 * Swagger属性配置类
 * </p>
 *
 * @author tianqingzhao
 * @since 2021/2/25 10:00
 */
@Data
@ConfigurationProperties(prefix = SwaggerProperties.SWAGGER_PREFIX)
public class SwaggerProperties {

    public static final String SWAGGER_PREFIX = "tqz.swagger";

    /**
     * 是否启用swagger,生产环境建议关闭
     */
    private boolean enabled;

    /**
     * 文档标题
     */
    private String title;

    /**
     * 文档描述
     */
    private String description;
}
