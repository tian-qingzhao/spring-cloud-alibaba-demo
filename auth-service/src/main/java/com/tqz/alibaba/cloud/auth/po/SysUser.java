package com.tqz.alibaba.cloud.auth.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.List;

/**
 * <p>
 * 用户
 * </p>
 *
 * @author tianqingzhao
 * @since 2021/3/6 15:18
 */
@Data
@TableName("sys_user")
public class SysUser {
    
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;
    
    private String username;
    
    private String password;
    
    private String role;
    
    private String mobile;
    
    @TableField(exist = false)
    private List<String> roles;
    
    @TableField(exist = false)
    private List<String> permissions;
}
