package com.tqz.alibaba.cloud.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tqz.alibaba.cloud.auth.po.SysPermission;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 权限 DAO层
 * </p>
 *
 * @author tianqingzhao
 * @since 2021/3/6 15:20
 */
public interface SysPermissionMapper extends BaseMapper<SysPermission> {

    /**
     * 查找角色对应的资源
     * @param roleIds 角色ids
     * @return 资源列表
     */
    List<SysPermission> listPermissionsByRoles(@Param("roleIds") List<Integer> roleIds);
}
