package com.tqz.cloud.alibaba.component.nacos;

import com.alibaba.cloud.sentinel.datasource.config.NacosDataSourceProperties;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.datasource.WritableDataSource;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.exception.NacosException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.alibaba.nacos.api.PropertyKeyConst.*;

/**
 * @author tianqingzhao
 * @since 2023/11/30 14:03
 */
public class NacosWritableDataSource<T> implements WritableDataSource<T> {

    private static final Logger log = LoggerFactory.getLogger(NacosWritableDataSource.class);

    private final String dataSourceName;

    private final Converter<T, String> converterEncoder;

    private final NacosDataSourceProperties nacosDataSourceProperties;

    private final String groupId;

    private final String dataId;

    private final Lock lock = new ReentrantLock(true);

    private static ConfigService configService = null;

    public NacosWritableDataSource(String dataSourceName,
                                   Converter<T, String> converterEncoder,
                                   NacosDataSourceProperties nacosDataSourceProperties) {
        this.dataSourceName = dataSourceName;
        this.converterEncoder = converterEncoder;
        this.nacosDataSourceProperties = nacosDataSourceProperties;
        this.groupId = nacosDataSourceProperties.getGroupId();
        this.dataId = nacosDataSourceProperties.getDataId();
    }

    @Override
    public void write(T value) throws Exception {
        lock.lock();
        try {
            String ruleContent = converterEncoder.convert(value);
            log.info("sentinel 客户端监听到控制台推送配置的变化, 数据源名称: {}, 规则类型: {}, 规则数据格式: {}, 规则内容: {}",
                    dataSourceName,
                    nacosDataSourceProperties.getRuleType().getName(),
                    nacosDataSourceProperties.getDataType(),
                    ruleContent);

            getConfigService().publishConfig(dataId, groupId, ruleContent, ConfigType.JSON.getType());
        } catch (Throwable throwable) {
            // ignore
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void close() throws Exception {
        // Nothing to do
    }

    private ConfigService getConfigService() {
        if (Objects.isNull(configService)) {
            synchronized (this) {
                try {
                    if (Objects.isNull(configService)) {
                        configService = NacosFactory.createConfigService(
                                assembleConfigServiceProperties());
                    }
                } catch (NacosException e) {
                    log.error(e.getMessage());
                    throw new IllegalArgumentException(nacosDataSourceProperties.getServerAddr(), e);
                }
            }
        }
        return configService;
    }

    public Properties assembleConfigServiceProperties() {
        Properties properties = new Properties();
        properties.put(SERVER_ADDR, Objects.toString(this.nacosDataSourceProperties.getServerAddr(), ""));
        properties.put(USERNAME, Objects.toString(this.nacosDataSourceProperties.getUsername(), ""));
        properties.put(PASSWORD, Objects.toString(this.nacosDataSourceProperties.getPassword(), ""));
        properties.put(NAMESPACE, Objects.toString(this.nacosDataSourceProperties.getNamespace(), ""));
        properties.put(ACCESS_KEY, Objects.toString(this.nacosDataSourceProperties.getAccessKey(), ""));
        properties.put(SECRET_KEY, Objects.toString(this.nacosDataSourceProperties.getSecretKey(), ""));
        String endpoint = Objects.toString(this.nacosDataSourceProperties.getEndpoint(), "");
        if (endpoint.contains(":")) {
            int index = endpoint.indexOf(":");
            properties.put(ENDPOINT, endpoint.substring(0, index));
            properties.put(ENDPOINT_PORT, endpoint.substring(index + 1));
        } else {
            properties.put(ENDPOINT, endpoint);
        }

        return properties;
    }
}
