package com.tqz.alibaba.cloud.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tqz.alibaba.cloud.auth.po.SysUser;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 用户 Dao层
 * </p>
 *
 * @author tianqingzhao
 * @since 2021/3/6 15:21
 */
public interface SysUserMapper extends BaseMapper<SysUser> {

    SysUser selectByUserName(@Param("userName") String userName);

    @Select("select *  from sys_user WHERE MOBILE = #{mobile}")
    SysUser selectByMobile(@Param("mobile") String mobile);

}
