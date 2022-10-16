package com.tqz.alibaba.cloud.order.feign.fallback;

import com.tqz.alibaba.cloud.common.base.ResultData;
import com.tqz.alibaba.cloud.common.dto.AccountDTO;
import com.tqz.alibaba.cloud.order.feign.AccountFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
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
public class AccountFallbackFactory implements FallbackFactory<AccountFeignClient> {

    @Override
    public AccountFeignClient create(Throwable cause) {
        return new AccountFeignClient() {
            
            @Override
            public ResultData<String> insert(AccountDTO accountDTO) {
                return new ResultData<String>().fail(cause.getMessage());
            }

            @Override
            public ResultData<String> delete(String accountCode) {
                return new ResultData<String>().fail(cause.getMessage());
            }

            @Override
            public ResultData<String> update(AccountDTO accountDTO) {
                return new ResultData<String>().fail(cause.getMessage());
            }

            @Override
            public ResultData<AccountDTO> getByCode(String accountCode) {
                return new ResultData<String>().fail(cause.getMessage());
            }

            @Override
            public ResultData<String> reduce(String accountCode, BigDecimal amount) {
                return new ResultData<String>().fail(cause.getMessage());
            }
        };
    }
}
