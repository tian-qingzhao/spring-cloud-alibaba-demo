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
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;

/**
 * 网关异常处理。
 *
 * @author tianqingzhao
 * @since 2023/1/14 11:57
 */
@Slf4j
@Component
public class AuthenticationEntryPointHandler implements ServerAuthenticationEntryPoint {
    
    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
        //获取请求路径
        String path = exchange.getRequest().getURI().getPath();
        log.error("身份验证失败，请求url：{}, 错误类型：{}", path, ex.getClass().getSuperclass().getName());
        // TODO 需要细化异常分类，不能统一返回
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
