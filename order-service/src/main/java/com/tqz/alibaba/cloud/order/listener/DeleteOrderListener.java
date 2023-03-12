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
 * 监听事务消息。
 *
 * <p>业务执行完之后向RocketMQ发送一个半消息，此时RocketMQ不会立马把消息发送给消费者，而是执行回查结果，
 * 根据回查结果返回的状态决定是否要向消费者发送消息。当然了rocketmq并不会无休止的的信息事务状态回查，
 * 默认回查15次，如果15次回查还是无法得知事务状态，RocketMQ默认回滚该消息。
 *
 * <p>RocketMQ处理事务的局限性：
 * 1、Rocketmq考虑的是数据最终一致性。上游服务提交之后，下游服务最终只能成功，做不到回滚上游数据。
 * 2、创建订单➕扣减库存，比如producer端是订单的创建，创建好发送消息到库存服务，库存扣减，但是库存为0扣减失败。
 * 这个时候RocketMQ是不支持数据TCC回滚的。针对这样的情况可以考虑使用阿里的Seata。
 *
 * @author tianqingzhao
 * @since 2021/2/26 10:00
 */
@Slf4j
@RocketMQTransactionListener
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DeleteOrderListener implements RocketMQLocalTransactionListener {

    private final OrderService orderService;

    private final RocketMqTransactionLogMapper rocketMqTransactionLogMapper;

    public static final String ORDER_ID_KEY = "order_id";

    /**
     * 执行本地事务。
     *
     * <p>正常事务发送与提交阶段:
     * 1、生产者发送一个半消息给MQServer（半消息是指消费者暂时不能消费的消息）
     * 2、服务端响应消息写入结果，半消息发送成功
     * 3、开始执行本地事务
     * 4、根据本地事务的执行状态执行Commit或者Rollback操作
     */
    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(final Message message, final Object arg) {
        log.info("执行本地事务");
        MessageHeaders headers = message.getHeaders();

        // 获取事务ID
        String transactionId = (String) headers.get(RocketMQHeaders.TRANSACTION_ID);

        String orderIdStr = null;
        if ((orderIdStr = (String) headers.get(ORDER_ID_KEY)) == null) {
            return RocketMQLocalTransactionState.ROLLBACK;
        }

        Integer orderId = Integer.valueOf(orderIdStr);
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
     *
     * <p>补偿阶段主要是用于解决生产者在发送Commit或者Rollback操作时发生超时或失败的情况，
     * 也就是说第一阶段 {@link #executeLocalTransaction(Message, Object)} 如果正常执行成功，这里是不会执行检查步骤的。
     *
     * <p>事务信息的补偿流程:
     * 1、如果MQServer长时间没收到本地事务的执行状态会向生产者发起一个确认回查的操作请求
     * 2、生产者收到确认回查请求后，检查本地事务的执行状态
     * 3、根据检查后的结果执行Commit或者Rollback操作
     */
    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(final Message message) {
        log.info("检查本地事务执行结果");

        MessageHeaders headers = message.getHeaders();

        // 获取事务ID
        String transactionId = (String) headers.get(RocketMQHeaders.TRANSACTION_ID);
        log.info("检查本地事务,事务ID:{}", transactionId);
        // 根据事务id从日志表检索
        QueryWrapper<RocketmqTransactionLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("transaction_id", transactionId);
        RocketmqTransactionLog rocketmqTransactionLog = rocketMqTransactionLogMapper.selectOne(queryWrapper);

        if (null != rocketmqTransactionLog) {
            log.info("检查本地事务成功，transactionId:{}", transactionId);
            return RocketMQLocalTransactionState.COMMIT;
        }
        log.info("检查本地事务失败，transactionId:{}", transactionId);
        return RocketMQLocalTransactionState.ROLLBACK;
    }

}
