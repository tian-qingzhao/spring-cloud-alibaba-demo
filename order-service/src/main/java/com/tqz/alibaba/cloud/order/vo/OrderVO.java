package com.tqz.alibaba.cloud.order.vo;

import com.tqz.alibaba.cloud.common.dto.AccountDTO;
import com.tqz.alibaba.cloud.common.dto.ProductDTO;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

/**
 * <p>
 *
 * @author tianqingzhao
 * @since 2022/12/29 13:36
 */
@Data
@ToString
@ApiModel(value = "订单展示层对象", description = "用于查询订单信息展示")
public class OrderVO {
    
    private AccountDTO accountDTO;
    
    private ProductDTO productDTO;
}
