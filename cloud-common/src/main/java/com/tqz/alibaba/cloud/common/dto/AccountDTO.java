package com.tqz.alibaba.cloud.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 账户信息接口传输层
 * </p>
 *
 * @author tianqingzhao
 * @since 2021/2/26 10:02
 */
@Data
@ToString
@ApiModel(value = "账户封装类AccountDTO", description = "账户相关信息封装，用于接口传参")
public class AccountDTO implements Serializable {
    
    @ApiModelProperty(value = "产品主键")
    private Integer id;
    
    @ApiModelProperty(value = "账户编码")
    private String accountCode;
    
    @ApiModelProperty(value = "账户姓名")
    private String accountName;
    
    @ApiModelProperty(value = "金额")
    private BigDecimal amount;
    
}
