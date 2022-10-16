package com.tqz.alibaba.cloud.order.listener;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tqz.alibaba.cloud.common.base.Constant;
import com.tqz.alibaba.cloud.order.mapper.RocketMqTransactionLogMapper;
import com.tqz.alibaba.cloud.order.po.RocketmqTransactionLog;
import com.tqz.alibaba.cloud.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

/**
 * <p>
 * 监听事务消息
 * </p>
 *
 * @author tianqingzhao
 * @since 2021/2/26 10:00
 */
@Slf4j
@RocketMQTransactionListener
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AddUserAmountListener implements RocketMQLocalTransactionListener {
    
    private final OrderService orderService;
    
    private final RocketMqTransactionLogMapper rocketMqTransactionLogMapper;
    
    /**
     * 执行本地事务
     */
    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message message, Object arg) {
        log.info("执行本地事务");
        MessageHeaders headers = message.getHeaders();
        // 获取事务ID
        String transactionId = (String) headers.get(RocketMQHeaders.TRANSACTION_ID);
        Integer orderId = Integer.valueOf((String) headers.get("order_id"));
        log.info("transactionId is {}, orderId is {}", transactionId, orderId);
        
        try {
            // 执行本地事务，并记录日志
            orderService.changeStatuswithRocketMqLog(orderId, Constant.INVALID_STATUS, transactionId);
            // 执行成功，可以提交事务
            return RocketMQLocalTransactionState.COMMIT;
        } catch (Exception e) {
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }
    
    /**
     * 本地事务的检查，检查本地事务是否成功
     */
    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message message) {
        
        MessageHeaders headers = message.getHeaders();
        // 获取事务ID
        String transactionId = (String) headers.get(RocketMQHeaders.TRANSACTION_ID);
        log.info("检查本地事务,事务ID:{}", transactionId);
        // 根据事务id从日志表检索
        QueryWrapper<RocketmqTransactionLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("transaction_id", transactionId);
        RocketmqTransactionLog rocketmqTransactionLog = rocketMqTransactionLogMapper.selectOne(queryWrapper);
        if (null != rocketmqTransactionLog) {
            return RocketMQLocalTransactionState.COMMIT;
        }
        return RocketMQLocalTransactionState.ROLLBACK;
    }
}
