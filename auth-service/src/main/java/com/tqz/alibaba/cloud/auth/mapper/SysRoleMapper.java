package com.tqz.alibaba.cloud.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tqz.alibaba.cloud.auth.po.SysRole;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 角色 Dao层
 * </p>
 *
 * @author tianqingzhao
 * @since 2021/3/6 15:21
 */
public interface SysRoleMapper extends BaseMapper<SysRole> {

    /**
     * 根据用户获取对应的角色列表
     * @param userId 用户id
     * @return List<SysRole>
     */
    @Select("select role.* from sys_user_role user_role left JOIN sys_role role on user_role.role_id = role.ID where user_role.user_id = #{userId}")
    List<SysRole> listRolesByUserId(@Param("userId")Integer userId);
}
