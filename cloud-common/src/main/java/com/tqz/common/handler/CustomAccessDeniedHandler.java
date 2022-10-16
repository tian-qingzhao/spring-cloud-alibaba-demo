/*
package com.tqz.handler;

import com.tqz.base.ResultData;
import com.tqz.base.ReturnCode;
import com.tqz.utils.WebUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

*/
/**
 * <p>
 * 认证过的用户访问无权限资源时的异常
 * </p>
 *
 * @author tianqingzhao
 * @since 2021/3/1 9:44
 *//*

@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException ex) throws IOException, ServletException {
        String requestUri = request.getRequestURI();
        log.error("access denied path,{}",requestUri);
        log.error("CustomDeniedHandler",ex);
        ResultData<String> resultData = ResultData.fail(ReturnCode.RC401.getCode(), ReturnCode.RC401.getMessage());
        resultData.setData(requestUri);

        WebUtils.writeJson(response,resultData);
    }
}
*/
