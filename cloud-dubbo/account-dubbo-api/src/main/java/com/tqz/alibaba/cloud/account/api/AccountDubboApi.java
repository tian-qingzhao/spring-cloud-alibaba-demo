package com.tqz.alibaba.cloud.account.api;

import com.tqz.alibaba.cloud.common.base.ResultData;
import com.tqz.alibaba.cloud.common.dto.AccountDTO;

/**
 * <p>account-service对外提供的dubbo接口。
 * 使用该模块需要先把该模块install。
 *
 * @author tianqingzhao
 * @since 2023/1/12 10:24
 */
public interface AccountDubboApi {
    
    /**
     * 根据账号编码查找账号详细信息
     *
     * @param accountCode 账号编码
     * @return 账户信息
     */
    ResultData<AccountDTO> getByCode(String accountCode);
}
