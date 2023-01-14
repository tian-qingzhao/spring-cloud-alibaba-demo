package com.tqz.alibaba.cloud.gateway.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tqz.alibaba.cloud.common.base.ResultData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.autoconfigure.web.reactive.error.ErrorWebFluxAutoConfiguration;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.handler.ResponseStatusExceptionHandler;
import reactor.core.publisher.Mono;

/**
 * <p>在 Spring Cloud Gateway 中默认使用 {@link DefaultErrorWebExceptionHandler} 来处理异常。
 * 这个可以通过配置类 {@link ErrorWebFluxAutoConfiguration} 得之。返回的数据内容在 {@link DefaultErrorAttributes} 类中构建而成。
 * <p>
 * 1.可以自定义一个 {@link CustomErrorWebExceptionHandler} 类用来继承 {@link DefaultErrorWebExceptionHandler}，
 * 然后修改生成前端响应数据的逻辑。再然后定义一个配置类，写法可以参考 {@link ErrorWebFluxAutoConfiguration} ，
 * 简单将异常类替换成 CustomErrorWebExceptionHandler 类即可，基本都是复制代码，改写不复杂。
 * 2.我们定义一个全局异常类 GlobalErrorWebExceptionHandler让其直接实现顶级接口 {@link ErrorWebExceptionHandler} 重写
 * handler() 方法， 在 handler() 方法中返回我们自定义的响应类。但是需要注意重写的实现类优先级一定
 * 要小于内置 {@link ResponseStatusExceptionHandler} 经过它处理的获取对应错误类的响应码。
 *
 * @author tianqingzhao
 * @since 2023/1/12 17:02
 */
@Slf4j
@Order(-1)
@Component
public class CustomErrorWebExceptionHandler implements ErrorWebExceptionHandler {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        if (response.isCommitted()) {
            return Mono.error(ex);
        }
        
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        if (ex instanceof ResponseStatusException) {
            response.setStatusCode(((ResponseStatusException) ex).getStatus());
        }
        
        return response.writeWith(Mono.fromSupplier(() -> {
            DataBufferFactory bufferFactory = response.bufferFactory();
            try {
                // 返回响应结果
                return bufferFactory.wrap(objectMapper.writeValueAsBytes(ResultData.fail(ex.getMessage())));
            } catch (JsonProcessingException e) {
                log.error("Error writing response", ex);
                return bufferFactory.wrap(new byte[0]);
            }
        }));
    }
}
