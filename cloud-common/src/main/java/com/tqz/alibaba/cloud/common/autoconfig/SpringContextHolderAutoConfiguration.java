package com.tqz.alibaba.cloud.common.autoconfig;

import com.tqz.alibaba.cloud.common.utils.SpringContextHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 *
 * @author tianqingzhao
 * @since 2023/1/10 15:52
 */
@Configuration
public class SpringContextHolderAutoConfiguration {
    
    @Bean
    public SpringContextHolder springContextHolder() {
        return new SpringContextHolder();
    }
}
