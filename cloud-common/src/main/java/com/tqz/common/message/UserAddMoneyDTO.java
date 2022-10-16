package com.tqz.common.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * <p>
 * 消息发送
 * </p>
 *
 * @author tianqingzhao
 * @since 2021/2/25 11:14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAddMoneyDTO {

    /**
     * 用户编码
     */
    private String userCode;

    /**
     * 金额
     */
    private BigDecimal amount;
}
