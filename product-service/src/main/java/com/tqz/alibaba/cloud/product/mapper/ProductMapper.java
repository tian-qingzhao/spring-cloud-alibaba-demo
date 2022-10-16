package com.tqz.alibaba.cloud.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tqz.alibaba.cloud.product.po.Product;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * Product Dao层
 * </p>
 *
 * @author tianqingzhao
 * @since 2021/2/25 10:14
 */
public interface ProductMapper extends BaseMapper<Product> {

    Product selectByCode(@Param("productCode") String productCode);

    int deleteByCode(@Param("productCode") String productCode);
}
