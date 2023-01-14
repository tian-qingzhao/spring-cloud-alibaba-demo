package com.tqz.alibaba.cloud.order.feign.fallback;

import com.tqz.alibaba.cloud.common.base.ResultData;
import com.tqz.alibaba.cloud.common.dto.ProductDTO;
import com.tqz.alibaba.cloud.order.feign.ProductFeignClient;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * <p>商品降级熔断类
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
                log.info("order服务插入商品接口异常");
                return ResultData.fail(cause.getMessage());
            }
            
            @Override
            public ResultData<String> delete(String productCode) {
                log.info("order服务删除商品接口异常");
                return ResultData.fail(cause.getMessage());
            }
            
            @Override
            public ResultData<String> update(ProductDTO productDTO) {
                log.info("order服务修改商品接口异常");
                return ResultData.fail(cause.getMessage());
            }
            
            @Override
            public ResultData<ProductDTO> getByCode(String productCode) {
                log.info("order服务查询商品接口异常");
                ProductDTO productDTO = new ProductDTO();
                productDTO.setId(1);
                productDTO.setProductName("熔断兜底商品");
                return ResultData.success(productDTO);
            }
            
            @Override
            public ResultData<BigDecimal> deduct(String productCode, Integer count) {
                log.info("order服务远程调用扣减商品接口异常");
                return ResultData.fail(cause.getMessage());
            }
        };
    }
}
