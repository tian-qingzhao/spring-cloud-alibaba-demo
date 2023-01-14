package com.tqz.alibaba.cloud.gateway.config.oauth2;

import com.alibaba.nacos.common.utils.ConcurrentHashSet;
import com.tqz.alibaba.cloud.common.base.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Set;

/**
 * <p>权限管理器。
 * 过滤掉静态资源，接口权限校验也可以放在这里。
 *
 * @author tianqingzhao
 * @since 2022/8/12 9:52
 */
@Component
@Slf4j
public class AccessManager implements ReactiveAuthorizationManager<AuthorizationContext>, InitializingBean {
    
    private final Set<String> permitAll = new ConcurrentHashSet<>();
    
    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();
    
    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }
    
    /**
     * 实现权限验证判断
     */
    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authenticationMono,
            AuthorizationContext authorizationContext) {
        ServerWebExchange exchange = authorizationContext.getExchange();
        // 请求资源
        String requestPath = exchange.getRequest().getURI().getPath();
        // 是否直接放行
        if (permitAll(requestPath)) {
            log.info("接口 {} 不需要校验权限，直接放行", requestPath);
            return Mono.just(new AuthorizationDecision(true));
        }
        
        return authenticationMono.map(auth -> new AuthorizationDecision(checkAuthorities(auth, requestPath)))
                .defaultIfEmpty(new AuthorizationDecision(false));
    }
    
    /**
     * 校验是否属于静态资源
     *
     * @param requestPath 请求路径
     * @return <code>true</code> 表示放行， <code>false</code> 表示不放行
     */
    private boolean permitAll(String requestPath) {
        return permitAll.stream().anyMatch(r -> ANT_PATH_MATCHER.match(r, requestPath));
    }
    
    /**
     * 权限校验
     *
     * @param auth        身份主体
     * @param requestPath 请求路径
     * @return <code>true</code> 表示有权限， <code>false</code> 表示无权限
     */
    private boolean checkAuthorities(Authentication auth, String requestPath) {
        if (auth instanceof OAuth2Authentication) {
            OAuth2Authentication athentication = (OAuth2Authentication) auth;
            String clientId = athentication.getOAuth2Request().getClientId();
            log.info("clientId is {}", clientId);
            
            Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
            boolean result = authorities.stream().map(GrantedAuthority::getAuthority)
                    .filter(item -> !item.startsWith(Constant.ROLE_PREFIX))
                    .anyMatch(permission -> ANT_PATH_MATCHER.match(permission, requestPath));
            log.info("判断接口 {} 权限结果：{}", requestPath, result);
            return result;
        }
        
        return false;
    }
    
    /**
     * 初始化静态资源以及不需要拦截的请求
     */
    private void init() {
        permitAll.add("/");
        permitAll.add("/error");
        permitAll.add("/favicon.ico");
        permitAll.add("/**/v2/api-docs/**");
        permitAll.add("/**/swagger-resources/**");
        permitAll.add("/webjars/**");
        permitAll.add("/doc.html");
        permitAll.add("/swagger-ui.html");
        permitAll.add("/**/oauth/**");
        permitAll.add("/**/current/get");
        permitAll.add("/**/smsCode/getSmsCode");
    }
}
