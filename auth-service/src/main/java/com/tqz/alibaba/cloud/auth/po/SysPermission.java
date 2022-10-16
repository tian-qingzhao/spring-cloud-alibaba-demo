package com.tqz.alibaba.cloud.auth.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * <p>
 * 权限
 * </p>
 *
 * @author tianqingzhao
 * @since 2021/3/6 15:18
 */
@Data
@TableName("sys_permission")
public class SysPermission {
    
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;
    
    private String name;
    
    private String permission;
    
    private String url;
}
