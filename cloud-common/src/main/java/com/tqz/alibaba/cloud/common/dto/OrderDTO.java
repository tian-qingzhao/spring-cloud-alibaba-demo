package com.tqz.alibaba.cloud.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 订单DTO
 * </p>
 *
 * @author tianqingzhao
 * @since 2021/2/26 10:00
 */
@Data
@ToString
@ApiModel(value = "订单封装类OrderDTO", description = "订单相关信息封装，用于接口传参")
public class OrderDTO implements Serializable {
    
    @ApiModelProperty(value = "订单主键")
    private Integer id;
    
    @ApiModelProperty(value = "账户编码")
    private String orderNo;
    
    @ApiModelProperty(value = "账户编码")
    private String accountCode;
    
    @ApiModelProperty(value = "产品编码")
    private String productCode;
    
    private Integer count;
    
    @ApiModelProperty(value = "总金额")
    private BigDecimal amount;
    
    @ApiModelProperty(value = "单价")
    private BigDecimal price;
    
    @ApiModelProperty(value = "订单状态")
    private String status;
}
