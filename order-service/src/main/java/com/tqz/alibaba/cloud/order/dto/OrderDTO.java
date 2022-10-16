package com.tqz.alibaba.cloud.order.dto;

import lombok.Data;

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
public class OrderDTO implements Serializable {
    private Integer id;
    private String orderNo;
    private String accountCode;
    private String productCode;
    private Integer count;
    //总金额
    private BigDecimal amount;
    //单价
    private BigDecimal price;
    private String status;
}
