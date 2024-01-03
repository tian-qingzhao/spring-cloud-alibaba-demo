package com.tqz.cloud.alibaba.component.circuitbreaker;

import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.ConfigBuilder;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * @author tianqingzhao
 * @since 2023/8/14 17:30
 */
@Component
public class CustomCircuitBreakerFactory extends CircuitBreakerFactory {

    @Override
    public CircuitBreaker create(String id) {
        return new CustomCircuitBreaker();
    }

    @Override
    protected ConfigBuilder configBuilder(String id) {
        return null;
    }

    @Override
    public void configureDefault(Function defaultConfiguration) {

    }
}
