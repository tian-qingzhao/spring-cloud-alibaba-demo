package com.tqz.alibaba.cloud.auth.sms;

import com.tqz.alibaba.cloud.auth.po.Pair;
import com.tqz.alibaba.cloud.auth.po.SysUser;
import com.tqz.alibaba.cloud.auth.service.ISysUserService;
import com.tqz.alibaba.cloud.common.base.Constant;
import com.tqz.alibaba.cloud.common.security.SecurityUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>验证码模式登录。
 *
 * <p>OAuth2官方默认有四种模式：
 * <li>1.授权码模式，即先登录获取code,再获取token。</li>
 * <li>2.密码模式，将用户名,密码传过去,直接获取token。</li>
 * <li>3.简化模式，在redirect_uri 的Hash传递token; Auth客户端运行在浏览器中,如JS,Flash。</li>
 * <li>4.客户端模式，无用户,用户向客户端注册,然后客户端以自己的名义向'服务端'获取资源。</li>
 * <li>5.Spring Security OAuth2 中扩展了一种 refresh_token 刷新token模式，refresh_token必须在过期之前调用才能换新的token。
 *      <i>刷新token时，如果access_token，refresh_token均未过期，access_token会是一个新的token,
 *      而且过期时间expires延长,refresh_token根据设定的过期时间,没有失效则不发生变化。</i>
 *      <i>刷新token时，如果access_token过期，refresh_token未过期，access_token会是一个新的token,
 *      而且过期时间expires延长,refresh_token根据设定的过期时间,没有失效则不发生变化。</i>
 *      <i>刷新token时，如果refresh_token过期，会返回401状态码，{"error":"invalid_token","error_description":"Invalid refresh token
 *      (expired):7f0cf53a-8b89-4a1f-b5ae-09b7e48dc888"}</i>
 * </li>
 * <li>6.这里自定义扩展一种验证码模式。</li>
 *
 * @author tianqingzhao
 * @since 2023/1/9 16:52
 */
@Slf4j
@Component
public class SmsCodeAuthenticationProvider implements AuthenticationProvider {
    
    @Autowired
    private InMemorySmsCodeCacheManager smsCodeCacheManager;
    
    @Autowired
    private ISysUserService userService;
    
    private static final String SMS_CODE_KEY = "smsCode";
    
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.info("SmsCodeAuthenticationProvider#authenticate开始认证");
        
        SmsCodeAuthenticationToken authenticationToken = (SmsCodeAuthenticationToken) authentication;
        
        String principal = (String) authenticationToken.getPrincipal();
        checkSmsCode(principal);
        
        SysUser sysUser = userService.getUserByMobile(principal);
        if (sysUser == null) {
            log.warn("手机号：{} 未查询到用户", principal);
            return null;
        }
        
        UserDetails userDetails = buildUserDetails(sysUser);
        return new SmsCodeAuthenticationToken(userDetails, userDetails.getAuthorities());
    }
    
    /**
     * {@link ProviderManager#authenticate(Authentication)} 选择具体 Provider 时根据此方法判断，
     * 只有当该方法返回 <code>true</code> 的时候才会执行 {@link #authenticate(Authentication)} 方法
     *
     * @param authentication 认证类
     * @return <code>true</code> 表示认证通过，<code>false</code> 表示认证失败。
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return SmsCodeAuthenticationToken.class.isAssignableFrom(authentication);
    }
    
    /**
     * 校验手机号验证码是否正确
     *
     * @param mobile 手机号
     */
    private void checkSmsCode(String mobile) {
        if (StringUtils.isEmpty(mobile)) {
            log.error("验证码模式登录，输入的手机号为空");
            throw new IllegalArgumentException("验证码模式登录，输入的手机号为空");
        }
        
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String smsCode = request.getParameter(SMS_CODE_KEY);
        log.info("获取用户输入的验证码：{}", smsCode);
        if (StringUtils.isEmpty(smsCode)) {
            log.error("手机号 {} 输入的验证码为空", mobile);
            throw new IllegalArgumentException("验证码不能为空");
        }
        
        Pair<Long, String> pair = smsCodeCacheManager.get(mobile);
        
        if (pair == null || !smsCode.equals(pair.getValue())) {
            log.error("根据手机号 {} 获取的验证码 {} 错误", mobile, smsCode);
            throw new IllegalArgumentException("验证码错误");
        }
    }
    
    /**
     * 构建用户认证信息
     *
     * @param sysUser 用户对象
     * @return UserDetails
     */
    private UserDetails buildUserDetails(SysUser sysUser) {
        Set<String> authSet = new HashSet<>();
        List<String> roles = sysUser.getRoles();
        if (!CollectionUtils.isEmpty(roles)) {
            roles.forEach(role -> authSet.add(Constant.ROLE_PREFIX + role));
            authSet.addAll(sysUser.getPermissions());
        }
        
        List<GrantedAuthority> authorityList = AuthorityUtils.createAuthorityList(authSet.toArray(new String[0]));
        
        return new SecurityUser(sysUser.getId(), sysUser.getMobile(), sysUser.getUsername(), sysUser.getPassword(),
                authorityList);
    }
}
