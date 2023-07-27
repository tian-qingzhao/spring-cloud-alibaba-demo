package com.tqz.alibaba.cloud.auth.sms;

import com.tqz.alibaba.cloud.auth.po.Pair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <p>基于内存的验证码缓存管理器
 *
 * @author tianqingzhao
 * @since 2023/1/10 9:30
 */
@Slf4j
@Component
public class InMemorySmsCodeCacheManager implements InitializingBean {

    private final Map<String, Pair<Long, String>> cache = new ConcurrentHashMap<>();

    /**
     * 55秒
     */
    private static final long SMS_CODE_EXPIRE_TIME = 55000000000L;

    @Override
    public void afterPropertiesSet() throws Exception {
        cleanOldSmsCode();
    }

    /**
     * 根据手机号获取验证码，如果验证码不存在就获取一个并丢入，如果存在就直接返回
     *
     * @param mobile 手机号
     * @return 纳秒的时间和验证码对象
     */
    public Pair<Long, String> getIfNotExitsGenerator(String mobile) {
        return cache.computeIfAbsent(mobile, this::generateSmsCode);
    }

    public Pair<Long, String> get(String mobile) {
        return cache.get(mobile);
    }

    /**
     * 生成验证码
     *
     * @param mobile 手机号
     * @return 纳秒的时间和验证码对象
     */
    private Pair<Long, String> generateSmsCode(String mobile) {
        long codeL = System.nanoTime();
        String codeStr = Long.toString(codeL);
        String smsCode = codeStr.substring(codeStr.length() - 6);

        log.info("手机号 {} 生成验证码 {}", mobile, smsCode);

        return new Pair<>(codeL, smsCode);
    }

    /**
     * 定时清除旧的验证码
     */
    private void cleanOldSmsCode() {
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);

        executor.scheduleWithFixedDelay(this::calculateIsExpire, 0, 1000, TimeUnit.MILLISECONDS);
    }

    /**
     * 计算是否过期，如果过期就移除。 TODO 此类计算方法会有误差
     */
    private void calculateIsExpire() {
        for (Map.Entry<String, Pair<Long, String>> pairEntry : cache.entrySet()) {
            String mobile = pairEntry.getKey();
            Long time = pairEntry.getValue().getKey();
            if (System.nanoTime() - time > SMS_CODE_EXPIRE_TIME) {
                log.info("删除手机号 {} 验证码", mobile);
                cache.remove(mobile);
            }
        }
    }

}
