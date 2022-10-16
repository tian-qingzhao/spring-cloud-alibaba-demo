package com.tqz.alibaba.cloud.auth.user;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * <p>自定义用户信息
 *
 * @author tianqingzhao
 * @since 2022/8/12 9:18
 */
public class SecurityUser extends User {
    @Getter
    private Integer id;

    @Getter
    private String mobile;

    public SecurityUser(Integer id, String mobile,
                        String username, String password,
                        Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.id = id;
        this.mobile = mobile;
    }

    @Override
    public String toString() {
        return "CustomUser{" +
                "id=" + id +
                ", mobile='" + mobile + '\'' +
                ", username='" + super.getUsername() + '\'' +
                '}';
    }
}
