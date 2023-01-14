package com.tqz.alibaba.cloud.account.config;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>自定义sentinel异常返回格式
 *
 * @author tianqingzhao
 * @since 2022/8/11 10:11
 */
@Slf4j
@Component
public class SentinelBlockExceptionHandler implements BlockExceptionHandler {
    
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, BlockException e) throws Exception {
        
        String message = "未知异常";
        int code = 429;
        
        if (e instanceof FlowException) {
            message = "请求被限流了";
        } else if (e instanceof ParamFlowException) {
            message = "请求被热点参数限流";
        } else if (e instanceof DegradeException) {
            message = "请求被降级了";
        } else if (e instanceof AuthorityException) {
            message = "没有权限访问";
            code = 403;
        }
        
        log.info("接口 {} {}", request.getRequestURI(), message);
        
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(code);
        response.getWriter().println("{\"message\": \"" + message + "\", \"code\": \"" + code + "\"}");
    }
}
