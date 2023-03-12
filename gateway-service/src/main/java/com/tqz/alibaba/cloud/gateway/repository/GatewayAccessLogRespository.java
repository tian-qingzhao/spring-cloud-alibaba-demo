package com.tqz.alibaba.cloud.gateway.repository;

import com.tqz.alibaba.cloud.gateway.entity.GatewayAccessLog;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * {@link GatewayAccessLog} 仓储接口。
 *
 * @author tianqingzhao
 * @since 2023/1/15 20:20
 */
@Repository
public interface GatewayAccessLogRespository extends ReactiveMongoRepository<GatewayAccessLog, String> {

}
