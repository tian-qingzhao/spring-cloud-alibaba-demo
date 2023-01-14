package com.tqz.alibaba.cloud.common.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collection;

/**
 * <p>自定义用户信息，继承 {@link User} 类，{@link User} 类实现 {@link UserDetails} 接口，
 * 在认证服务器(auth-service)服务里面通过 {@link UserDetailsService} 接口的实现类返回该类扩展的额外信息。
 *
 * @author tianqingzhao
 * @since 2022/8/12 9:18
 */
@Getter
public class SecurityUser extends User {
    
    private Integer id;
    
    private String mobile;
    
    private String author;
    
    public SecurityUser(Integer id, String mobile, String username, String password,
            Collection<? extends GrantedAuthority> authorities) {
        this(id, mobile, username, password, null, authorities);
    }
    
    public SecurityUser(Integer id, String mobile, String username, String password, String author,
            Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.id = id;
        this.mobile = mobile;
        this.author = author;
    }
    
    @Override
    public String toString() {
        return "SecurityUser{" + "id=" + id + ", mobile='" + mobile + '\'' + ", author='" + author + '\''
                + ", username='" + super.getUsername() + '\'' + '}';
    }
}
