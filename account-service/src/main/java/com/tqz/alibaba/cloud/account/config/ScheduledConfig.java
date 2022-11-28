package com.tqz.alibaba.cloud.account.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * <p>定時任務配置類，测试使用 {@link RefreshScope} 注解热更新配置的时候，
 * 结合 {@link Scheduled} 注解失效的场景。
 * 解决方法：监听 {@link RefreshScopeRefreshedEvent} 事件，该事件在spring cloud发布完配置变更事件之后，
 * 具体实现 {@link org.springframework.cloud.context.scope.refresh.RefreshScope#refreshAll}
 *
 *
 * @author tianqingzhao
 * @since 2022/10/16 13:01
 */
//@Component
@RefreshScope
public class ScheduledConfig implements ApplicationListener<RefreshScopeRefreshedEvent> {

    private static final Logger log = LoggerFactory.getLogger(ScheduledConfig.class);

    @Value("${test}")
    private String name;
    
    @GetMapping
    public String get() {
        return name;
    }

    @Scheduled(cron = "*/5 * * * * ?")
    public void test() {
        log.info("执行定时任务，获取到配置:{}", name);
    }
    
    @Override
    public void onApplicationEvent(RefreshScopeRefreshedEvent event) {
        log.info("定时任务配置类监听到配置变更");
    }
}
