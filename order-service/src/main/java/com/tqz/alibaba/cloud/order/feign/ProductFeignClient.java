package com.tqz.alibaba.cloud.order.feign;

import com.tqz.alibaba.cloud.common.base.Constant;
import com.tqz.alibaba.cloud.common.base.ResultData;
import com.tqz.alibaba.cloud.common.dto.ProductDTO;
import com.tqz.alibaba.cloud.order.feign.fallback.ProductFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/**
 * <p>
 * ProductFeign 对外暴露接口
 * </p>
 *
 * @author tianqingzhao
 * @since 2021/2/26 10:02
 */
@FeignClient(path = "/product", value = Constant.PRODUCT_SERVICE_NAME, fallbackFactory = ProductFallbackFactory.class)
public interface ProductFeignClient {

    /**
     * 新增产品
     *
     * @param productDTO
     * @return
     */
    @PostMapping("/insert")
    ResultData<String> insert(@RequestBody ProductDTO productDTO);

    /**
     * 删除产品
     *
     * @param productCode
     * @return
     */
    @PostMapping("/delete")
    ResultData<String> delete(@RequestParam("productCode") String productCode);

    /**
     * 编辑产品
     *
     * @param productDTO
     * @return
     */
    @PostMapping("/update")
    ResultData<String> update(@RequestBody ProductDTO productDTO);

    /**
     * 查找产品
     *
     * @param productCode
     * @return
     */
    @GetMapping("/getByCode/{productCode}")
    ResultData<ProductDTO> getByCode(@PathVariable(value = "productCode") String productCode);

    /**
     * 扣减库存
     *
     * @param productCode
     * @param count
     * @return
     */
    @PostMapping("/deduct")
    ResultData<BigDecimal> deduct(@RequestParam("productCode") String productCode, @RequestParam("count") Integer count);
}
