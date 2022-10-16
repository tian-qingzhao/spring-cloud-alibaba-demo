package com.tqz.alibaba.cloud.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tqz.alibaba.cloud.auth.po.SysPermission;

import java.util.List;

/**
 * <p>
 * 权限服务接口
 * </p>
 *
 * @author tianqingzhao
 * @since 2021/3/6 15:18
 */
public interface ISysPermissionService extends IService<SysPermission> {
    
    /**
     * 获取所有角色的权限
     *
     * @param roleIds 角色id列表
     * @return 角色列表
     */
    List<SysPermission> listPermissionsByRoles(List<Integer> roleIds);
}
