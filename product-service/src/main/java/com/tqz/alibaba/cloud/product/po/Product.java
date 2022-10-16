package com.tqz.alibaba.cloud.product.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * <p>
 * Product实体类
 * </p>
 *
 * @author tianqingzhao
 * @since 2021/2/25 10:14
 */
@Data
@TableName("product")
public class Product {

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    private String productCode;

    private String productName;

    private Integer count;

    private BigDecimal price;
}
