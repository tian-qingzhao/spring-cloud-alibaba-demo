package com.tqz.alibaba.cloud.order.config;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.RequestOriginParser;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleChecker;
import com.tqz.alibaba.cloud.common.base.Constant;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>自定义请求来源类型，用于在sentinel持久化规则配置 `limitApp` 属性。
 * 该属性默认值为 <code>""</code>，如果 `limitApp` 属性不是默认的<code>default</code>，
 * 就需要实现 {@link RequestOriginParser}接口， {@link #parseOrigin(HttpServletRequest)}方法返回的要和 `limitApp` 配置的值保持一致。
 * <p>
 * 限流对比两个字段处理逻辑：{@link FlowRuleChecker#selectNodeByRequesterAndStrategy}
 *
 * @author tianqingzhao
 * @since 2022/8/10 13:39
 */
@Component
public class OrderServiceRequestOriginParser implements RequestOriginParser {
    
    @Override
    public String parseOrigin(HttpServletRequest request) {
        return Constant.ORDER_SERVICE_NAME;
    }
}
