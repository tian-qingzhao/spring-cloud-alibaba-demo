package com.tqz.alibaba.cloud.auth.service.impl;

import com.tqz.alibaba.cloud.auth.mapper.SysUserMapper;
import com.tqz.alibaba.cloud.auth.po.SysPermission;
import com.tqz.alibaba.cloud.auth.po.SysRole;
import com.tqz.alibaba.cloud.auth.po.SysUser;
import com.tqz.alibaba.cloud.auth.service.ISysPermissionService;
import com.tqz.alibaba.cloud.auth.service.ISysRoleService;
import com.tqz.alibaba.cloud.auth.service.ISysUserService;
import com.tqz.alibaba.cloud.common.base.Constant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 系统用户服务层实现类
 * </p>
 *
 * @author tianqingzhao
 * @since 2021/3/6 15:22
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SysUserServiceImpl implements ISysUserService {
    
    private final SysUserMapper sysUserMapper;
    
    private final ISysRoleService sysRoleService;
    
    private final ISysPermissionService sysPermissionService;
    
    @Override
    public SysUser getUserByMobile(String mobile) {
        log.info("SysUserServiceImpl#getUserByMobile()开始执行");
        
        SysUser sysUser = sysUserMapper.selectByMobile(mobile);
        if (sysUser != null) {
            // 获取当前用户的所有角色
            List<SysRole> roleList = sysRoleService.listRolesByUserId(sysUser.getId());
            sysUser.setRoles(roleList.stream().map(SysRole::getRoleCode).collect(Collectors.toList()));
            List<Integer> roleIds = roleList.stream().map(SysRole::getId).collect(Collectors.toList());
            // 获取所有角色的权限
            List<SysPermission> permissionList = sysPermissionService.listPermissionsByRoles(roleIds);
            
            // 1.网关做权限的校验，这里返回用户所拥有的url，网关处根据使用 `AccessManager` 类对请求的url判断是否有权限访问
            // 拼接请求方式，用于解决RestApi的权限问题
            sysUser.setPermissions(permissionList.stream()
                    .map(sysPermission -> Constant.BRACKETS_PREFIX + sysPermission.getMethod()
                            + Constant.BRACKETS_SUFFIX + sysPermission.getUrl()).collect(Collectors.toList()));
            
            // 2.业务微服务自定义权限校验，需要把数据库的 `permission` 字段给返回，网关的 `AccessManager` 类全部要放行
            // 并且在每个微服务提供的接口处添加 Spring Security 注解 @PreAuthorize("hasPrivilege('queryAccount')") ，
            // 该注解的 hasPrivilege 里面的值需要与 `permission` 字段的值保持一致。
            // 每个微服务还需要使用 `@EnableGlobalMethodSecurity(prePostEnabled = true)` 注解来开启权限的校验
            //            sysUser.setPermissions(
            //                    permissionList.stream().map(SysPermission::getPermission).collect(Collectors.toList()));
            
            log.info("用户所拥有的权限：{}", permissionList.stream().map(SysPermission::getUrl).collect(Collectors.toList()));
        }
        return sysUser;
    }
}
