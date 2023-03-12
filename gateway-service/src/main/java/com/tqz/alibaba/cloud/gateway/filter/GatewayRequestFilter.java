package com.tqz.alibaba.cloud.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nimbusds.jose.JWSObject;
import com.tqz.alibaba.cloud.common.base.Constant;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.text.ParseException;

/**
 * 某些特定的接口防止请求不通过微服务远程调用。
 *
 * <p>比如一个请求的流程：网关-》订单服务-》账户服务，此时可能会存在：网关-》账户服务，
 * 也就是不经过订单服务通过RPC调用了，这时候可通过该过滤器进行判断。
 * 账户服务的接口需要添加 `/pv` 路径，订单服务的不需要添加，
 * 这里判断接口如果包含该路径就拦截，不包含就表示是订单服务调用，直接放行。
 *
 * <p>Java通常由以下词语表示不同含义：
 * 1.pb - public 所有请求均可访问
 * 2.pt - protected 需要进行token认证通过后方可访问
 * 3.pv - private 无法通过网关访问，只能微服务内部调用
 * 4.df - default 网关请求token认证，并且请求参数和返回结果进行加解密
 *
 * @author tianqingzhao
 * @since 2023/3/9 11:43
 */
@Component
@Order(0)
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GatewayRequestFilter implements GlobalFilter {

    private final RedisTemplate<String, String> redisTemplate;

    @SneakyThrows
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //获取请求路径
        String rawPath = exchange.getRequest().getURI().getRawPath();

        //获取请求头的token
        String headerToken = exchange.getRequest().getHeaders().getFirst(Constant.JWT_AUTHORIZATION_HEADER_KEY);

        if (StringUtils.isNotEmpty(headerToken)) {
            // 是否在黑名单
            if (isBlack(headerToken)) {
                throw new HttpServerErrorException(HttpStatus.FORBIDDEN, "该令牌已过期，请重新获取令牌");
            }
        }

        if (isPv(rawPath)) {
            throw new HttpServerErrorException(HttpStatus.FORBIDDEN, "can't access private API");
        }

        return chain.filter(exchange);
    }

    /**
     * 判断是否内部私有方法
     *
     * @param requestUri 请求路径
     * @return boolean
     */
    private boolean isPv(String requestUri) {
        return isAccess(requestUri);
    }

    /**
     * 网关访问控制校验
     *
     * @param requestUri 请求uri
     * @return boolean
     */
    private boolean isAccess(String requestUri) {
        // 后端标准请求路径为 /访问控制/请求路径
        int index = requestUri.indexOf("/pv");
        return index > 0;
    }

    /**
     * 通过redis判断token是否为黑名单
     *
     * @param headerToken 请求头
     * @return boolean
     */
    private boolean isBlack(String headerToken) throws ParseException {
        //todo  移除所有oauth2相关代码，暂时使用 OAuth2AccessToken.BEARER_TYPE 代替
        String token = headerToken.replace(OAuth2AccessToken.BEARER_TYPE, StringUtils.EMPTY).trim();

        //解析token
        JWSObject jwsObject = JWSObject.parse(token);
        String payload = jwsObject.getPayload().toString();
        JSONObject jsonObject = JSON.parseObject(payload);

        // JWT唯一标识
        String jti = jsonObject.getString("jti");
        return redisTemplate.hasKey(Constant.REDIS_TOKEN_BLACKLIST_PREFIX + jti);
    }
}
