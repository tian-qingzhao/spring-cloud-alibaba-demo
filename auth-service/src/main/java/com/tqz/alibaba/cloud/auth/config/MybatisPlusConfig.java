package com.tqz.alibaba.cloud.auth.config;
import com.baomidou.mybatisplus.core.parser.ISqlParser;
import com.baomidou.mybatisplus.extension.parsers.BlockAttackSqlParser;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PerformanceInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Mybatis-Plus配置类
 * </p>
 *
 * @author tianqingzhao
 * @since 2021/2/26 9:54
 */
@Configuration
@MapperScan("com.tqz.alibaba.cloud.auth.mapper")
public class MybatisPlusConfig {

    /**
     * 攻击 SQL 阻断解析器
     */
    @Bean
    public PaginationInterceptor paginationInterceptor(){
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        List<ISqlParser> sqlParserList = new ArrayList<>();
        sqlParserList.add(new BlockAttackSqlParser());

        paginationInterceptor.setSqlParserList(sqlParserList);
        return new PaginationInterceptor();
    }

    /**
     * SQL执行效率插件
     */
    @Bean
//    @Profile({"dev","test"})
    public PerformanceInterceptor performanceInterceptor() {
        return new PerformanceInterceptor();
    }
}
