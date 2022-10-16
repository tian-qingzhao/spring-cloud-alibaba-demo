package com.tqz.alibaba.cloud.order.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * <p>
 * order实体类
 * </p>
 *
 * @author tianqingzhao
 * @since 2021/2/26 10:01
 */
@Data
@TableName("t_order")
public class Order {
    
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;
    
    private String orderNo;
    
    private String accountCode;
    
    private String productCode;
    
    private Integer count;
    
    private BigDecimal amount;
    
    private String status;
}
