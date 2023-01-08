package com.tqz.alibaba.cloud.order.feign.fallback;

import com.tqz.alibaba.cloud.common.base.ResultData;
import com.tqz.alibaba.cloud.common.dto.ProductDTO;
import com.tqz.alibaba.cloud.order.feign.ProductFeignClient;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * <p>
 *
 * </p>
 *
 * @author tianqingzhao
 * @since 2021/2/26 11:58
 */
@Slf4j
@Component
public class ProductFallbackFactory implements FallbackFactory<ProductFeignClient> {

    @Override
    public ProductFeignClient create(Throwable cause) {
        return new ProductFeignClient() {
    
            @Override
            public ResultData<String> insert(ProductDTO productDTO) {
                return new ResultData<String>().fail(cause.getMessage());
            }
    
            @Override
            public ResultData<String> delete(String productCode) {
                return new ResultData<String>().fail(cause.getMessage());
            }
    
            @Override
            public ResultData<String> update(ProductDTO productDTO) {
                return new ResultData<String>().fail(cause.getMessage());
            }
    
            @Override
            public ResultData<ProductDTO> getByCode(String productCode) {
                return new ResultData<String>().fail(cause.getMessage());
            }
    
            @Override
            public ResultData<BigDecimal> deduct(String productCode, Integer count) {
                return new ResultData<BigDecimal>().fail(cause.getMessage());
            }
        };
    }
}
