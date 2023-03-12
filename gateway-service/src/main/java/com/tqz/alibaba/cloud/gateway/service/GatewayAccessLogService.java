package com.tqz.alibaba.cloud.gateway.service;

import com.tqz.alibaba.cloud.gateway.entity.GatewayAccessLog;
import reactor.core.publisher.Mono;

/**
 * <p>
 *
 * @author tianqingzhao
 * @since 2023/1/15 20:21
 */
public interface GatewayAccessLogService {
    
    Mono<GatewayAccessLog> insertLog(GatewayAccessLog log);
}
