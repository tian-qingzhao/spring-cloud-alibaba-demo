package com.tqz.alibaba.cloud.gateway.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * 网关获取请求和响应的日志实体类。
 *
 * @author tianqingzhao
 * @since 2023/1/15 20:09
 */
@Data
@Document
public class GatewayAccessLog {
    
    /**
     * id
     */
    @Id
    private String id;
    
    /**
     * 访问实例
     */
    private String targetServer;
    
    /**
     * 请求路径
     */
    private String requestPath;
    
    /**
     * 请求方法
     */
    private String requestMethod;
    
    /**
     * 协议
     */
    private String schema;
    
    /**
     * 请求体
     */
    private String requestBody;
    
    /**
     * 响应体
     */
    private String responseData;
    
    /**
     * 请求ip
     */
    private String ip;
    
    /**
     * 请求时间
     */
    private Date requestTime;
    
    /**
     * 响应时间
     */
    private Date responseTime;
    
    /**
     * 执行时间
     */
    private long executeTime;
}