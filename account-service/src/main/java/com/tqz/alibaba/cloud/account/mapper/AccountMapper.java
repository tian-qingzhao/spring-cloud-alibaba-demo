package com.tqz.alibaba.cloud.account.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tqz.alibaba.cloud.account.po.Account;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * <p>
 * Account Dao层
 * </p>
 *
 * @author tianqingzhao
 * @since 2021/2/26 9:55
 */
public interface AccountMapper extends BaseMapper<Account> {

    Account selectByCode(@Param("accountCode") String accountCode);

    int deleteByCode(@Param("accountCode") String accountCode);

    void increaseAmount(@Param("accountCode") String accountCode, @Param("amount")BigDecimal amount);
}
