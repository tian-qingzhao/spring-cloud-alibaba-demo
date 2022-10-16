package com.tqz.alibaba.cloud.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tqz.alibaba.cloud.auth.po.SysRole;

import java.util.List;

/**
 * <p>
 * 角色服务接口
 * </p>
 *
 * @author tianqingzhao
 * @since 2021/3/6 15:18
 */
public interface ISysRoleService extends IService<SysRole> {
    
    /**
     * 根据用户id获取所有角色列表
     *
     * @param userId 用户id
     * @return roleList
     */
    List<SysRole> listRolesByUserId(Integer userId);
}
