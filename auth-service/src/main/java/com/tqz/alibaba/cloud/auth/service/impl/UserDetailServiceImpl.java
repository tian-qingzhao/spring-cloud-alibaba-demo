package com.tqz.alibaba.cloud.auth.service.impl;

import com.tqz.alibaba.cloud.auth.mapper.SysUserMapper;
import com.tqz.alibaba.cloud.auth.po.SysPermission;
import com.tqz.alibaba.cloud.auth.po.SysRole;
import com.tqz.alibaba.cloud.auth.po.SysUser;
import com.tqz.alibaba.cloud.auth.service.ISysPermissionService;
import com.tqz.alibaba.cloud.auth.service.ISysRoleService;
import com.tqz.alibaba.cloud.auth.user.SecurityUser;
import com.tqz.alibaba.cloud.common.base.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 自定义用户实现
 * </p>
 *
 * @author tianqingzhao
 * @since 2021/3/6 14:59
 */
@Service
public class UserDetailServiceImpl implements UserDetailsService {
    
    @Autowired
    private SysUserMapper sysUserMapper;
    
    @Autowired
    private ISysRoleService sysRoleService;
    
    @Autowired
    private ISysPermissionService sysPermissionService;
    
    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        //获取本地用户
        SysUser sysUser = sysUserMapper.selectByUserName(userName);
        if (sysUser != null) {
            //获取当前用户的所有角色
            List<SysRole> roleList = sysRoleService.listRolesByUserId(sysUser.getId());
            sysUser.setRoles(roleList.stream().map(SysRole::getRoleCode).collect(Collectors.toList()));
            List<Integer> roleIds = roleList.stream().map(SysRole::getId).collect(Collectors.toList());
            //获取所有角色的权限
            List<SysPermission> permissionList = sysPermissionService.listPermissionsByRoles(roleIds);
            sysUser.setPermissions(permissionList.stream().map(SysPermission::getUrl).collect(Collectors.toList()));
            
            //构建oauth2的用户
            return buildUserDetails(sysUser);
        } else {
            throw new UsernameNotFoundException("用户[" + userName + "]不存在");
        }
    }
    
    /**
     * 构建oAuth2用户，将角色和权限赋值给用户，角色使用ROLE_作为前缀
     *
     * @param sysUser 系统用户
     * @return
     */
    private UserDetails buildUserDetails(SysUser sysUser) {
        Set<String> authSet = new HashSet<>();
        List<String> roles = sysUser.getRoles();
        if (!CollectionUtils.isEmpty(roles)) {
            roles.forEach(item -> authSet.add(Constant.ROLE_PREFIX + item));
            authSet.addAll(sysUser.getPermissions());
        }
        
        List<GrantedAuthority> authorityList = AuthorityUtils.createAuthorityList(authSet.toArray(new String[0]));
        
        return new SecurityUser(sysUser.getId(), sysUser.getMobile(), sysUser.getUsername(), sysUser.getPassword(),
                authorityList);
    }
}
