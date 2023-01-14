package com.tqz.alibaba.cloud.product.dubbo;

import com.tqz.alibaba.cloud.common.base.ResultData;
import com.tqz.alibaba.cloud.common.dto.ProductDTO;
import com.tqz.alibaba.cloud.product.api.ProductDubboApi;
import com.tqz.alibaba.cloud.product.mapper.ProductMapper;
import com.tqz.alibaba.cloud.product.po.Product;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * product服务对外提供dubbo接口。内部之间可使用dubbo，外部调用可使用feign。
 *
 * @author tianqingzhao
 * @since 2023/1/13 16:46
 */
@Service
public class ProductDubboApiImpl implements ProductDubboApi {
    
    @Autowired
    private ProductMapper productMapper;
    
    @Override
    public ResultData<ProductDTO> getByCode(String productCode) {
        ProductDTO productVO = new ProductDTO();
        Product product = productMapper.selectByCode(productCode);
        BeanUtils.copyProperties(product, productVO);
        return ResultData.success(productVO);
    }
}
