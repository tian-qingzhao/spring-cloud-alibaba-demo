package com.tqz.cloud.alibaba.component.circuitbreaker;

import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.stereotype.Component;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author tianqingzhao
 * @since 2023/8/14 10:50
 */
@Component
public class CustomCircuitBreaker implements CircuitBreaker {

    @Override
    public <T> T run(Supplier<T> toRun, Function<Throwable, T> fallback) {
        try {
            return toRun.get();
        } catch (Exception e) {
            return fallback.apply(e);
        }
    }
}
