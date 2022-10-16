package com.tqz.alibaba.cloud.auth.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tqz.alibaba.cloud.auth.mapper.SysRoleMapper;
import com.tqz.alibaba.cloud.auth.po.SysRole;
import com.tqz.alibaba.cloud.auth.service.ISysRoleService;
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
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements ISysRoleService {

    @Override
    public List<SysRole> listRolesByUserId(Integer userId) {
        return baseMapper.listRolesByUserId(userId);
    }
}
