package com.tqz.alibaba.cloud.gateway.config.oauth2;

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
 * <p>自定义授权认证，从JWT中获取token。
 *
 * 使用 jwt token 和 access_token 最大的区别就是资源服务器不再需要去认证服务器校验token，提升了系统整体性能，使用jwt后项目的流程架构如下：
 * 1.客户端请求网关
 * 2.网关转发到OAuth2认证服务器(auth-service)
 * 3.OAuth2认证服务器返回access_token给网关
 * 4.网关返回给客户端
 * 5.客户端携带access_token请求网关
 * 6.网关校验access_token
 * 7.网关转发到对应的业务微服务(account-service/product-service/order-service)
 *
 * <p>JWT的缺点：jwt是一次性的，一旦token被签发，那么在到期时间之前都是有效的，无法废弃。
 * 如果中途修改了用户权限需要更新信息那就只能重新签发一个jwt，但是旧的jwt还是可以正常使用，使用旧的jwt拿到的信息也就是过时的。
 * jwt包含了认证信息，一旦泄露，任何人都可以获得该令牌的所有权限。为了防止盗用，jwt的有效时间不应该设置过长。
 *
 * @author tianqingzhao
 * @since 2023/1/9 14:29
 */
@Slf4j
public class JwtReactiveAuthenticationManager implements ReactiveAuthenticationManager {
    
    private final TokenStore tokenStore;
    
    public JwtReactiveAuthenticationManager(TokenStore tokenStore) {
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
                    // 根据 access_token 从数据库获取不到 OAuth2AccessToken
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
