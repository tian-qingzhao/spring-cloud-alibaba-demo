package com.tqz.cloud.alibaba.component.sentinel.datasource.nacos;

import com.alibaba.cloud.sentinel.SentinelProperties;
import com.alibaba.cloud.sentinel.datasource.config.NacosDataSourceProperties;
import com.alibaba.csp.sentinel.command.handler.ModifyParamFlowRulesCommandHandler;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.csp.sentinel.slots.system.SystemRule;
import com.alibaba.csp.sentinel.transport.util.WritableDataSourceRegistry;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Sentinel 客户端往 Nacos 写数据源的初始化类.
 *
 * @author tianqingzhao
 * @since 2023/11/30 17:13
 */
@Component
public class NacosWritableDataSourceInit {

    private static final Logger log = LoggerFactory.getLogger(NacosWritableDataSourceInit.class);

    @Autowired
    private SentinelProperties sentinelProperties;

    @PostConstruct
    public void init() {
        log.info("开始注册nacos作为sentinel写规则的持久化数据源");
        initNacosWritableDataSource();
    }

    public void initNacosWritableDataSource() {
        sentinelProperties.getDatasource()
                .forEach((dataSourceName, dataSourceProperties) -> {
                    NacosDataSourceProperties nacosDataSourceProperties = dataSourceProperties.getNacos();

                    switch (nacosDataSourceProperties.getRuleType()) {
                        case FLOW:
                            Converter<List<FlowRule>, String> flowRules = JSON::toJSONString;
                            NacosWritableDataSource<List<FlowRule>> nacosWritableDataSourceFlow =
                                    new NacosWritableDataSource<>(dataSourceName, flowRules, nacosDataSourceProperties);
                            WritableDataSourceRegistry.registerFlowDataSource(nacosWritableDataSourceFlow);
                            break;
                        case DEGRADE:
                            Converter<List<DegradeRule>, String> degradeRules = JSON::toJSONString;
                            NacosWritableDataSource<List<DegradeRule>> nacosWritableDataSourceDegrade =
                                    new NacosWritableDataSource<>(dataSourceName, degradeRules, nacosDataSourceProperties);
                            WritableDataSourceRegistry.registerDegradeDataSource(nacosWritableDataSourceDegrade);
                            break;
                        case PARAM_FLOW:
                            Converter<List<ParamFlowRule>, String> paramFlowRules = JSON::toJSONString;
                            NacosWritableDataSource<List<ParamFlowRule>> nacosWritableDataSourceParamFlow =
                                    new NacosWritableDataSource<>(dataSourceName, paramFlowRules, nacosDataSourceProperties);
                            ModifyParamFlowRulesCommandHandler.setWritableDataSource(nacosWritableDataSourceParamFlow);
                            break;
                        case SYSTEM:
                            Converter<List<SystemRule>, String> systemRules = JSON::toJSONString;
                            NacosWritableDataSource<List<SystemRule>> nacosWritableDataSourceSystem =
                                    new NacosWritableDataSource<>(dataSourceName, systemRules, nacosDataSourceProperties);
                            WritableDataSourceRegistry.registerSystemDataSource(nacosWritableDataSourceSystem);
                            break;
                        case AUTHORITY:
                            Converter<List<AuthorityRule>, String> authorityRules = JSON::toJSONString;
                            NacosWritableDataSource<List<AuthorityRule>> nacosWritableDataSourceAuthority =
                                    new NacosWritableDataSource<>(dataSourceName, authorityRules, nacosDataSourceProperties);
                            WritableDataSourceRegistry.registerAuthorityDataSource(nacosWritableDataSourceAuthority);
                            break;
                        case GW_FLOW:
                            break;
                        case GW_API_GROUP:
                            break;
                        default:
                            break;
                    }
                });
    }
}
