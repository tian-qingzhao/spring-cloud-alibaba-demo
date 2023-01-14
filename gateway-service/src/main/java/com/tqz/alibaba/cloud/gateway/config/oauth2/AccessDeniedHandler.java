package com.tqz.alibaba.cloud.gateway.config.oauth2;

import com.alibaba.fastjson.JSONObject;
import com.tqz.alibaba.cloud.common.base.ResultData;
import com.tqz.alibaba.cloud.common.base.ReturnCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authorization.HttpStatusServerAccessDeniedHandler;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;

/**
 * 网关处理认证用户无权限时的异常。
 *
 * <p>Spring Security默认的异常处理器实现为 {@link HttpStatusServerAccessDeniedHandler} ，这里更改默认的实现，
 * 且需要把该类设置到 {@link SecurityConfig} 配置类里面的 {@link SecurityWebFilterChain} 里。
 *
 * @author tianqingzhao
 * @since 2023/1/13 14:39
 */
@Slf4j
@Component
public class AccessDeniedHandler implements ServerAccessDeniedHandler {
    
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException ex) {
        // 获取请求路径
        String path = exchange.getRequest().getURI().getPath();
        log.error("身份无权限访问，请求url：{}，错误类型：{}", path, ex.getClass().getSuperclass().getName());
        
        ResultData<String> resultData = ResultData.fail(ReturnCode.RC403.getCode(), ReturnCode.RC403.getMessage());
        resultData.setData(path);
        
        return Mono.defer(() -> Mono.just(exchange.getResponse())).flatMap((response) -> {
            response.setStatusCode(HttpStatus.valueOf(resultData.getCode()));
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            DataBufferFactory dataBufferFactory = response.bufferFactory();
            DataBuffer buffer = dataBufferFactory.wrap(
                    JSONObject.toJSONString(resultData).getBytes(Charset.defaultCharset()));
            return response.writeWith(Mono.just(buffer)).doOnError((error) -> {
                DataBufferUtils.release(buffer);
            });
        });
    }
}
