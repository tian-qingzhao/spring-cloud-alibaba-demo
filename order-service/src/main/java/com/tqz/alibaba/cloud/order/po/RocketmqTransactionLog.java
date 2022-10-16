package com.tqz.alibaba.cloud.order.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * rocketmq事务日志实体类
 * </p>
 *
 * @author tianqingzhao
 * @since 2021/2/26 10:01
 */
@Data
@TableName("rocketmq_transaction_log")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RocketmqTransactionLog {
    
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;
    
    private String transactionId;
    
    private String log;
}
