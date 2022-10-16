package com.tqz.alibaba.cloud.auth.service.impl;

import com.tqz.alibaba.cloud.auth.mapper.SysUserMapper;
import com.tqz.alibaba.cloud.auth.po.SysPermission;
import com.tqz.alibaba.cloud.auth.po.SysRole;
import com.tqz.alibaba.cloud.auth.po.SysUser;
import com.tqz.alibaba.cloud.auth.service.ISysPermissionService;
import com.tqz.alibaba.cloud.auth.service.ISysRoleService;
import com.tqz.alibaba.cloud.auth.service.ISysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *
 * </p>
 *
 * @author tianqingzhao
 * @since 2021/3/6 15:22
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SysUserServiceImpl implements ISysUserService {
    
    private final SysUserMapper sysUserMapper;
    
    private final ISysRoleService sysRoleService;
    
    private final ISysPermissionService sysPermissionService;
    
    @Override
    public SysUser getUserByMobile(String mobile) {
        SysUser sysUser = sysUserMapper.selectByMobile(mobile);
        if (sysUser != null) {
            //获取当前用户的所有角色
            List<SysRole> roleList = sysRoleService.listRolesByUserId(sysUser.getId());
            sysUser.setRoles(roleList.stream().map(SysRole::getRoleCode).collect(Collectors.toList()));
            List<Integer> roleIds = roleList.stream().map(SysRole::getId).collect(Collectors.toList());
            //获取所有角色的权限
            List<SysPermission> permissionList = sysPermissionService.listPermissionsByRoles(roleIds);
            
            sysUser.setPermissions(
                    permissionList.stream().map(SysPermission::getPermission).collect(Collectors.toList()));
        }
        return sysUser;
    }
}
