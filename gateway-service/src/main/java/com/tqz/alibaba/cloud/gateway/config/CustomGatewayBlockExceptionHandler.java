package com.tqz.alibaba.cloud.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.exception.SentinelGatewayBlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.tqz.alibaba.cloud.common.base.ResultData;
import com.tqz.alibaba.cloud.common.base.ReturnCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;


/**
 * <p>网关自定义限流返回。
 * Sentinel整合Spring Cloud Gateway默认限流方式在 {@link SentinelGatewayBlockExceptionHandler} 类里面，
 * 也是实现了 {@link WebExceptionHandler} 接口，所以想自定义返回格式，只需要重写该接口即可。
 *
 * @author tianqingzhao
 * @since 2021/3/3 17:01
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class CustomGatewayBlockExceptionHandler implements WebExceptionHandler, Ordered {
    
    @Autowired
    private InetUtils inetUtils;
    
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        if (exchange.getResponse().isCommitted()) {
            return Mono.error(ex);
        } else {
            return !BlockException.isBlockException(ex) ? Mono.error(ex) : this.handleBlockedRequest(exchange, ex)
                    .flatMap((response) -> this.writeResponse(response, exchange));
        }
    }
    
    private Mono<ServerResponse> handleBlockedRequest(ServerWebExchange exchange, Throwable throwable) {
        log.error("网关拦截到请求url：{} 被限流了", exchange.getRequest().getURI().getPath());
        return GatewayCallbackManager.getBlockHandler().handleRequest(exchange, throwable);
    }
    
    private Mono<Void> writeResponse(ServerResponse response, ServerWebExchange exchange) {
        ServerHttpResponse serverHttpResponse = exchange.getResponse();
        serverHttpResponse.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        ResultData<Object> resultData = ResultData.fail(ReturnCode.RC201.getCode(), ReturnCode.RC201.getMessage());
        String resultString = JSON.toJSONString(resultData);
        DataBuffer buffer = serverHttpResponse.bufferFactory().wrap(resultString.getBytes());
        return serverHttpResponse.writeWith(Mono.just(buffer));
    }
    
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
    
    //解决sentinel控制台不能显示API管理问题
    //    @PostConstruct
    //    public void doInit() {
    //        System.setProperty(TransportConfig.HEARTBEAT_CLIENT_IP, inetUtils.findFirstNonLoopbackAddress().getHostAddress());
    ////        SentinelConfig.setConfig(TransportConfig.HEARTBEAT_CLIENT_IP, inetUtils.findFirstNonLoopbackAddress().getHostAddress());
    //    }
    
}
