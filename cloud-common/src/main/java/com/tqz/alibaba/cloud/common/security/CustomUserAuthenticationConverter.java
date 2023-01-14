package com.tqz.alibaba.cloud.common.security;

import com.tqz.alibaba.cloud.common.base.Constant;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Map;

/**
 * 自定义Token解析。
 *
 * <p>默认情况下 {@link JwtAccessTokenConverter} 会调用
 * {@link DefaultUserAuthenticationConverter#extractAuthentication(Map)} 方法从token中获取用户信息。
 * 在没有注入 {@link UserDetailsService}的情况下oauth2只会获取用户名 user_name。
 * 如果注入了 UserDetailsService 就可以返回所有用户信息。所以这里我们对应的实现方式也有两种：
 * <li>1.在资源服务器中也注入 UserDetailsService ，这种方法不推荐，
 * 资源服务器与认证服务器分开的情况下强行耦合在一起，也需要加入用户认证的功能。</li>
 * <li>
 * 2.扩展 {@link DefaultUserAuthenticationConverter}，重写 {@link #extractAuthentication(Map)} 方法，
 * 手动取出额外数据，然后在资源服务器配置中将其注入到AccessTokenConverter中。
 * </li>
 *
 * 自定义JWT增强信息扩展流程如下：
 * 1.在认证服务器auth-service里面通过 {@link UserDetailsService} 实现类，
 * 并在返回的用户信息 {@link SecurityUser} 类继承 {@link User}  类，可灵活添加额外信息
 * 2.在认证服务器auth-service服务里面添加 `CustomJwtAccessTokenConverter` 类并继承 {@link JwtAccessTokenConverter} 类，
 * 并在 enhance 方法中丢入第一步添加的信息。同时把 `CustomJwtAccessTokenConverter` 类配置在 `AuthorizationServerConfig` 配置类里面注入到spring容器。
 * 3.在该类 {@link CustomUserAuthenticationConverter} 类里面获取用户的信息，并把该类 设置到 {@link CustomAccessTokenConverter} 类里，
 * 同时把 {@link CustomAccessTokenConverter} 类配置到资源服务器(account-service)服务，
 * 配置步骤为在 `ResourceServerConfig` 配置类给 {@link JwtAccessTokenConverter} 添加 {@link CustomAccessTokenConverter} 。
 * 4.资源服务器可以通过 `SecurityContextHolder.getContext().getAuthentication().getPrincipal()` 获取用户的额外信息了。
 *
 * @author tianqingzhao
 * @since 2022/8/12 9:19
 */
public class CustomUserAuthenticationConverter extends DefaultUserAuthenticationConverter {
    
    /**
     * 重写抽取认证信息
     *
     * @param map 用户认证信息
     * @return Authentication
     */
    @Override
    public Authentication extractAuthentication(Map<String, ?> map) {
        if (map.containsKey(USERNAME)) {
            Collection<? extends GrantedAuthority> authorities = getAuthorities(map);
            
            String username = (String) map.get(USERNAME);
            Integer id = (Integer) map.get(Constant.USER_ID_KEY);
            String mobile = (String) map.get(Constant.MOBILE_KEY);
            String author = (String) map.get(Constant.AUTHOR_KEY);
            SecurityUser user = new SecurityUser(id, mobile, username, "N/A", author, authorities);
            return new UsernamePasswordAuthenticationToken(user, "N/A", authorities);
        }
        return null;
    }
    
    protected Collection<? extends GrantedAuthority> getAuthorities(Map<String, ?> map) {
        Object authorities = map.get(AUTHORITIES);
        if (authorities instanceof String) {
            return AuthorityUtils.commaSeparatedStringToAuthorityList((String) authorities);
        }
        if (authorities instanceof Collection) {
            return AuthorityUtils.commaSeparatedStringToAuthorityList(
                    StringUtils.collectionToCommaDelimitedString((Collection<?>) authorities));
        }
        throw new IllegalArgumentException("Authorities must be either a String or a Collection");
    }
}
