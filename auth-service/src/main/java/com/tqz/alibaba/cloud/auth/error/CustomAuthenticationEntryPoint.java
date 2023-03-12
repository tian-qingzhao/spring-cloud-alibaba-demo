package com.tqz.alibaba.cloud.auth.error;

import com.tqz.alibaba.cloud.auth.config.AuthorizationServerConfig;
import com.tqz.alibaba.cloud.common.base.ResultData;
import com.tqz.alibaba.cloud.common.base.ReturnCode;
import com.tqz.alibaba.cloud.common.utils.WebUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 自定义OAuth2客户端异常返回信息，也就是client_id、client_secret不正确的情况。
 *
 * <p>在 {@link AuthorizationServerConfig#configure(AuthorizationServerSecurityConfigurer security)} 配置类方法里面
 * 把该类设置到自定义的 {@link CustomClientCredentialsTokenEndpointFilter} 过滤器里面。
 *
 * @author tianqingzhao
 * @since 2023/1/15 17:50
 */
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final String CLIENT_ID_KEY = "client_id";

    private static final String CLIENT_SECRET_KEY = "client_secret";

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        log.error("认证服务器获取access_token失败，{}：{}，或者{}：{} 不正确",
                CLIENT_ID_KEY, request.getParameter(CLIENT_ID_KEY),
                CLIENT_SECRET_KEY, request.getParameter(CLIENT_SECRET_KEY));

        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        ResultData<String> resultData = ResultData.fail(ReturnCode.CLIENT_AUTHENTICATION_FAILED.getCode(),
                ReturnCode.CLIENT_AUTHENTICATION_FAILED.getMessage());

        WebUtils.writeJson(response, resultData);
    }
}
