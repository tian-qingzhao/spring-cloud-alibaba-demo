package com.tqz.alibaba.cloud.gateway.service;

import com.tqz.alibaba.cloud.gateway.entity.GatewayAccessLog;
import com.tqz.alibaba.cloud.gateway.repository.GatewayAccessLogRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * <p>
 *
 * @author tianqingzhao
 * @since 2023/1/15 20:24
 */
@Service
public class GatewayAccessLogServiceImpl implements GatewayAccessLogService {
    
    @Autowired
    private GatewayAccessLogRespository accessLogRespository;
    
    @Override
    public Mono<GatewayAccessLog> insertLog(GatewayAccessLog log) {
        return accessLogRespository.insert(log);
    }
}
