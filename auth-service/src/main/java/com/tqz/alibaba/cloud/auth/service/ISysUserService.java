package com.tqz.alibaba.cloud.auth.service;

import com.tqz.alibaba.cloud.auth.po.SysUser;

/**
 * <p>
 * 用户服务接口
 * </p>
 *
 * @author tianqingzhao
 * @since 2021/3/6 15:19
 */
public interface ISysUserService {
    
    /**
     * 根据手机号码获取用户信息
     *
     * @param mobile 手机号码
     * @return 用户信息
     */
    SysUser getUserByMobile(String mobile);
}
