package com.tqz.alibaba.cloud.auth.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tqz.alibaba.cloud.auth.mapper.SysPermissionMapper;
import com.tqz.alibaba.cloud.auth.po.SysPermission;
import com.tqz.alibaba.cloud.auth.service.ISysPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission>
        implements ISysPermissionService {
    
    @Override
    public List<SysPermission> listPermissionsByRoles(List<Integer> roleIds) {
        return baseMapper.listPermissionsByRoles(roleIds);
    }
}
