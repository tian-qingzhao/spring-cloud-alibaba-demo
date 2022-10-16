package com.tqz.alibaba.cloud.product.service;

import com.tqz.alibaba.cloud.common.base.ResultData;
import com.tqz.alibaba.cloud.common.dto.ProductDTO;

import java.math.BigDecimal;

/**
 * <p>
 * Product Service 接口层
 * </p>
 *
 * @author tianqingzhao
 * @since 2021/2/25 10:15
 */
public interface ProductService {

    /**
     * 新增产品
     */
    ProductDTO insertProduct(ProductDTO accountVO);

    /**
     * 删除产品
     */
    int deleteProduct(String accountCode);

    /**
     * 更新产品
     */
    ProductDTO updateProduct(ProductDTO accountVO);

    /**
     * 根据产品编码查找产品详细信息
     */
    ProductDTO selectByCode(String productCode);

    /**
     * 扣减库存
     */
    ResultData<BigDecimal> deduct(String productCode, Integer count);
}
