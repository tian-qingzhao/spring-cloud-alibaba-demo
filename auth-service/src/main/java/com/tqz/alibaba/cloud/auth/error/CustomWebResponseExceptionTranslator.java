package com.tqz.alibaba.cloud.auth.error;

import com.tqz.alibaba.cloud.auth.config.AuthorizationServerConfig;
import com.tqz.alibaba.cloud.common.base.ResultData;
import com.tqz.alibaba.cloud.common.base.ReturnCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.common.exceptions.UnsupportedGrantTypeException;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;

/**
 * 自定义OAuth2的账号密码错误、授权模式错误。
 *
 * <p>Spring Security OAuth2 官方默认使用的 {@link DefaultWebResponseExceptionTranslator} 。
 * 入口为 {@link TokenEndpoint} 一旦有错误，就会抛出 {@link OAuth2Exception} 异常。
 *
 * 最后需要把该类注入到 {@link AuthorizationServerConfig#configure(AuthorizationServerEndpointsConfigurer)} 配置类里面。
 *
 * @author tianqingzhao
 * @since 2023/1/15 17:05
 */
@Slf4j
public class CustomWebResponseExceptionTranslator implements WebResponseExceptionTranslator {
    
    @Override
    public ResponseEntity<ResultData<String>> translate(Exception e) throws Exception {
        log.error("认证服务器异常", e);
        
        ResultData<String> response = resolveException(e);
        
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }
    
    /**
     * 构建返回异常
     *
     * @param e exception
     * @return 响应体
     */
    private ResultData<String> resolveException(Exception e) {
        // 初始值 500
        ReturnCode returnCode = ReturnCode.RC500;
        int httpStatus = HttpStatus.UNAUTHORIZED.value();
        
        // 不支持的认证方式
        if (e instanceof UnsupportedGrantTypeException) {
            returnCode = ReturnCode.UNSUPPORTED_GRANT_TYPE;
            // 用户名或密码异常
        } else if (e instanceof InvalidGrantException) {
            returnCode = ReturnCode.USERNAME_OR_PASSWORD_ERROR;
        }
        
        ResultData<String> failResponse = ResultData.fail(returnCode.getCode(), returnCode.getMessage());
        failResponse.setCode(httpStatus);
        
        return failResponse;
    }
}
