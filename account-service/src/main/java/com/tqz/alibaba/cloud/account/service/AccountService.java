package com.tqz.alibaba.cloud.account.service;

import com.tqz.alibaba.cloud.common.dto.AccountDTO;
import com.tqz.alibaba.cloud.common.base.ResultData;

import java.math.BigDecimal;

/**
 * <p>
 * Account Service 接口层
 * </p>
 *
 * @author tianqingzhao
 * @since 2021/2/26 9:26
 */
public interface AccountService {

    /**
     * 新增用户
     *
     * @param accountDTO 账户传输类
     */
    void insertAccount(AccountDTO accountDTO);

    /**
     * 删除用户
     *
     * @param accountCode 账号编码
     */
    void deleteAccount(String accountCode);

    /**
     * 更新用户
     *
     * @param accountDTO 账户传输类
     */
    void updateAccount(AccountDTO accountDTO);

    /**
     * 根据用户编码查找用户详细信息
     *
     * @param accountCode 账号编码
     * @return
     */
    AccountDTO selectByCode(String accountCode);

    /**
     * 扣减账户余额
     *
     * @param accountCode 账号编码
     * @param amount      扣减的金额
     * @return
     */
    ResultData<String> reduceAccount(String accountCode, BigDecimal amount);
}
