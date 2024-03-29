package com.tqz.alibaba.cloud.common.handler;

import com.tqz.alibaba.cloud.common.base.ResultData;
import com.tqz.alibaba.cloud.common.base.ReturnCode;
import com.tqz.alibaba.cloud.common.utils.WebUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p>
 * 自定义匿名用户访问无权限资源时的异常
 * </p>
 *
 * @author tianqingzhao
 * @since 2021/3/1 9:45
 */
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException ex) throws IOException, ServletException {
        String accessToken = request.getHeader("authorization");
        String requestUri = request.getRequestURI();
        log.error("AuthenticationEntryPoint Path is {},access_token is {}",requestUri,accessToken);
        log.error("CustomAuthenticationEntryPoint",ex);
        ResultData<Object> resultData = ResultData.fail(ReturnCode.RC401.getCode(), ReturnCode.RC401.getMessage());
        resultData.setData(requestUri);
        response.setStatus(resultData.getCode());

        WebUtils.writeJson(response,resultData);
    }
}

