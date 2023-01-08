package com.tqz.alibaba.cloud.gateway.oauth2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import reactor.core.publisher.Mono;

/**
 * <p>自定义授权认证，从JDBC中获取token
 *
 * @author tianqingzhao
 * @since 2022/8/12 9:45
 */
@Slf4j
public class JdbcReactiveAuthenticationManager implements ReactiveAuthenticationManager {
    
    private final TokenStore tokenStore;
    
    public JdbcReactiveAuthenticationManager(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }
    
    /**
     * token检验
     */
    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.justOrEmpty(authentication).filter(a -> a instanceof BearerTokenAuthenticationToken)
                .cast(BearerTokenAuthenticationToken.class).map(BearerTokenAuthenticationToken::getToken)
                .flatMap((accessToken -> {
                    log.info("accessToken is :{}", accessToken);
                    OAuth2AccessToken oAuth2AccessToken = this.tokenStore.readAccessToken(accessToken);
                    // 根据access_token从数据库获取不到OAuth2AccessToken
                    if (oAuth2AccessToken == null) {
                        return Mono.error(new InvalidTokenException("无效的token，请检查token是否正确"));
                    } else if (oAuth2AccessToken.isExpired()) {
                        return Mono.error(new InvalidTokenException("token已过期，请重新获取token"));
                    }
                    
                    OAuth2Authentication oAuth2Authentication = this.tokenStore.readAuthentication(accessToken);
                    if (oAuth2Authentication == null) {
                        return Mono.error(new InvalidTokenException("Access Token 无效!"));
                    } else {
                        return Mono.just(oAuth2Authentication);
                    }
                })).cast(Authentication.class);
    }
}
