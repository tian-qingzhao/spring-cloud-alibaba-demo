package com.tqz.alibaba.cloud.product.api;

import com.tqz.alibaba.cloud.common.base.ResultData;
import com.tqz.alibaba.cloud.common.dto.ProductDTO;

/**
 * <p>product-service对外提供的dubbo接口。
 * 使用该模块需要先把该模块install。
 *
 * @author tianqingzhao
 * @since 2023/1/13 16:38
 */
public interface ProductDubboApi {
    
    /**
     * 根据商品编码查找商品详细信息
     *
     * @param productCode 商品编码
     * @return 账户信息
     */
    ResultData<ProductDTO> getByCode(String productCode);
}
