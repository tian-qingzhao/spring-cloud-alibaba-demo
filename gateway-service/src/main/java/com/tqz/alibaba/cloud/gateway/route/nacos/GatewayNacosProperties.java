package com.tqz.alibaba.cloud.gateway.route.nacos;

import com.alibaba.nacos.api.common.Constants;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * <p>网关使用nacos做动态路由的配置属性类
 *
 * @author tianqingzhao
 * @since 2022/8/28 12:19
 */
@Configuration
@ConfigurationProperties(GatewayNacosProperties.PREFIX)
public class GatewayNacosProperties {
    
    /**
     * 前缀
     */
    public static final String PREFIX = "gateway.nacos";
    
    /**
     * nacos服务端地址
     */
    private String serverAddr = "localhost:8848";
    
    /**
     * 命名空间
     */
    private String namespace = Constants.DEFAULT_NAMESPACE_ID;
    
    /**
     * dataId
     */
    private String dataId;
    
    /**
     * 分组名称
     */
    private String group = Constants.DEFAULT_GROUP;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 密码
     */
    private String password;
    
    /**
     * 拉取配置超时时间，单位毫秒
     */
    private long timeoutMs = 3000L;
    
    /**
     * 是否开启，默认开启
     */
    private boolean enabled = true;
    
    public String getServerAddr() {
        return serverAddr;
    }
    
    public void setServerAddr(String serverAddr) {
        this.serverAddr = serverAddr;
    }
    
    public String getNamespace() {
        return namespace;
    }
    
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
    
    public String getDataId() {
        return dataId;
    }
    
    public void setDataId(String dataId) {
        this.dataId = dataId;
    }
    
    public String getGroup() {
        return group;
    }
    
    public void setGroup(String group) {
        this.group = group;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public long getTimeoutMs() {
        return timeoutMs;
    }
    
    public void setTimeoutMs(long timeoutMs) {
        this.timeoutMs = timeoutMs;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
