package com.tqz.alibaba.cloud.auth.controller;

import com.tqz.alibaba.cloud.auth.dto.LoginRequest;
import com.tqz.alibaba.cloud.auth.dto.LoginTypeEnum;
import com.tqz.alibaba.cloud.common.base.Constant;
import com.tqz.alibaba.cloud.common.base.ResultData;
import com.tqz.alibaba.cloud.common.base.ReturnCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.util.Assert;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>自定义登录和退出控制器
 *
 * @author tianqingzhao
 * @since 2023/3/11 17:36
 */
@RestController
@RequestMapping("/token")
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthController {

    private final TokenStore tokenStore;

    private final TokenEndpoint tokenEndpoint;

    private final RedisTemplate<String, String> redisTemplate;

    @PostMapping("login")
    public ResultData<OAuth2AccessToken> login(LoginRequest loginRequest) throws HttpRequestMethodNotSupportedException {
        Assert.isTrue(StringUtils.isNotEmpty(loginRequest.getGrantType()), "请在参数中指定登录类型grantType");

        LoginTypeEnum typeEnum = LoginTypeEnum.fromGrantType(loginRequest.getGrantType());

        //注入clientId 和 password
        // 可以通过Header传入client 和 secret
        User clientUser = new User(loginRequest.getClientId(), loginRequest.getClientSecret(), new ArrayList<>());
        Authentication token = new UsernamePasswordAuthenticationToken(clientUser, null, new ArrayList<>());

        //构建密码登录
        Map<String, String> map = new HashMap<>();

        switch (typeEnum) {
            case PASSWORD: {
                map.put("username", loginRequest.getUserName());
                map.put("password", loginRequest.getPassword());
                map.put("grant_type", "password");
                break;
            }
            case SMSCODE: {
                map.put("smsCode", loginRequest.getSmsCode());
                map.put("mobile", loginRequest.getMobile());
                map.put("grant_type", "sms_code");
                break;
            }
            default:
                throw new IllegalArgumentException("不支持的登录类型");
        }

        OAuth2AccessToken oAuth2AccessToken = tokenEndpoint.postAccessToken(token, map).getBody();
        return ResultData.success(oAuth2AccessToken);

    }

    /**
     * 用户退出登录
     *
     * @param authHeader 从请求头获取token
     * @author jianzhang11
     */
    @DeleteMapping("/logout")
    public ResultData<String> logout(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String authHeader) {

        //获取token，去除前缀
        String token = authHeader.replace(OAuth2AccessToken.BEARER_TYPE, "").trim();

        // 解析Token
        OAuth2AccessToken oAuth2AccessToken = tokenStore.readAccessToken(token);

        //token 已过期
        if (oAuth2AccessToken.isExpired()) {
            return ResultData.fail(ReturnCode.INVALID_TOKEN_OR_EXPIRED.getCode(),
                    ReturnCode.INVALID_TOKEN_OR_EXPIRED.getMessage());
        }

        if (StringUtils.isBlank(oAuth2AccessToken.getValue())) {
            //访问令牌不合法
            return ResultData.fail(ReturnCode.INVALID_TOKEN.getCode(), ReturnCode.INVALID_TOKEN.getMessage());
        }

        OAuth2Authentication oAuth2Authentication = tokenStore.readAuthentication(oAuth2AccessToken);

        String userName = oAuth2Authentication.getName();

        //获取token唯一标识
        String jti = (String) oAuth2AccessToken.getAdditionalInformation().get("jti");

        //获取过期时间
        Date expiration = oAuth2AccessToken.getExpiration();
        long exp = expiration.getTime() / 1000;

        long currentTimeSeconds = System.currentTimeMillis() / 1000;

        //设置token过期时间
        redisTemplate.opsForValue().set(Constant.REDIS_TOKEN_BLACKLIST_PREFIX + jti, userName,
                (exp - currentTimeSeconds), TimeUnit.SECONDS);

        return ResultData.success("退出成功");
    }
}
