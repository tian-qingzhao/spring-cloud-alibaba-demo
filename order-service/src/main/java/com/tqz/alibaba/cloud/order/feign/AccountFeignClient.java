package com.tqz.alibaba.cloud.order.feign;

import com.tqz.alibaba.cloud.common.base.Constant;
import com.tqz.alibaba.cloud.common.base.ResultData;
import com.tqz.alibaba.cloud.common.dto.AccountDTO;
import com.tqz.alibaba.cloud.order.feign.fallback.AccountFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/**
 * <p>
 * AccountFeign
 * </p>
 *
 * @author tianqingzhao
 * @since 2021/2/26 9:56
 */
@FeignClient(path = "account", name = Constant.ACCOUNT_SERVICE_NAME, fallbackFactory = AccountFallbackFactory.class)
public interface AccountFeignClient {

    @PostMapping("/insert")
    ResultData<String> insert(@RequestBody AccountDTO accountDTO);

    @PostMapping("/delete")
    ResultData<String> delete(@RequestParam("accountCode") String accountCode);

    @PostMapping("/update")
    ResultData<String> update(@RequestBody AccountDTO accountDTO);

    @GetMapping("/getByCode")
    ResultData<AccountDTO> getByCode(@RequestParam(value = "accountCode") String accountCode);

    @PostMapping("reduce")
    ResultData<String> reduce(@RequestParam("accountCode") String accountCode, @RequestParam("amount") BigDecimal amount);

}
