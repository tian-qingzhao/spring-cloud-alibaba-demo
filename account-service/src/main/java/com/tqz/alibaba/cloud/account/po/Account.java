package com.tqz.alibaba.cloud.account.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * <p>
 * 用户类
 * </p>
 *
 * @author tianqingzhao
 * @since 2021/2/26 9:27
 */
@Data
@TableName("account")
public class Account {
    
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;
    
    private String accountCode;
    
    private String accountName;
    
    private BigDecimal amount;
}
