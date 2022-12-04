package com.tqz.alibaba.cloud.order.service.impl;

import com.tqz.alibaba.cloud.common.base.Constant;
import com.tqz.alibaba.cloud.common.base.ResultData;
import com.tqz.alibaba.cloud.common.base.ReturnCode;
import com.tqz.alibaba.cloud.common.exception.ServiceException;
import com.tqz.alibaba.cloud.common.dto.UserAddMoneyDTO;
import com.tqz.alibaba.cloud.order.dto.OrderDTO;
import com.tqz.alibaba.cloud.order.feign.AccountFeignClient;
import com.tqz.alibaba.cloud.order.feign.ProductFeignClient;
import com.tqz.alibaba.cloud.order.mapper.OrderMapper;
import com.tqz.alibaba.cloud.order.mapper.RocketMqTransactionLogMapper;
import com.tqz.alibaba.cloud.order.po.Order;
import com.tqz.alibaba.cloud.order.po.RocketmqTransactionLog;
import com.tqz.alibaba.cloud.order.service.OrderService;
import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * <p>
 * 订单的服务实现类
 * </p>
 *
 * @author tianqingzhao
 * @since 2021/2/26 10:01
 */
@Service
@Log4j2
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrderServiceImpl implements OrderService {
    
    private final OrderMapper orderMapper;
    
    private final RocketMQTemplate rocketMQTemplate;
    
    private final RocketMqTransactionLogMapper rocketMqTransactionLogMapper;
    
    private final AccountFeignClient accountFeignClient;
    
    private final ProductFeignClient productFeignClient;
    
    @GlobalTransactional(name = "TX_ORDER_CREATE")
    @Override
    public ResultData<OrderDTO> createOrder(OrderDTO orderDTO) {
        orderDTO.setOrderNo(UUID.randomUUID().toString());
        
        log.info("ORDER XID is: {}", RootContext.getXID());
        
        ResultData<BigDecimal> productResult = productFeignClient.deduct(orderDTO.getProductCode(),
                orderDTO.getCount());
        if (productResult.getCode() == ReturnCode.RC100.getCode()) {
            ResultData<String> accountResult = accountFeignClient.reduce(orderDTO.getAccountCode(),
                    productResult.getData());
            if (accountResult.getCode() == ReturnCode.RC100.getCode()) {
                Order order = new Order();
                BeanUtils.copyProperties(orderDTO, order);
                order.setAmount(productResult.getData());
                // 本地存储Order
                ((OrderServiceImpl) AopContext.currentProxy()).saveOrder(order);
                return ResultData.success("下单成功！");
            }
        }
        
        throw new ServiceException("下单失败！");
    }
    
    @Transactional(rollbackFor = RuntimeException.class)
    public void saveOrder(Order order) {
        orderMapper.insert(order);
    }
    
    @Override
    public OrderDTO selectByNo(String orderNo) {
        OrderDTO orderDTO = new OrderDTO();
        Order order = orderMapper.selectByNo(orderNo);
        BeanUtils.copyProperties(order, orderDTO);
        return orderDTO;
    }
    
    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public void changeStatus(Integer id, String status) {
        orderMapper.changeStatus(id, status);
    }
    
    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public void changeStatuswithRocketMqLog(Integer id, String status, String transactionId) {
        orderMapper.changeStatus(id, status);
        rocketMqTransactionLogMapper.insert(
                RocketmqTransactionLog.builder().transactionId(transactionId).log("执行删除订单操作").build());
    }
    
    @Override
    public void delete(String orderNo) {
        Order order = orderMapper.selectByNo(orderNo);
        // 如果订单存在且状态为有效，进行业务处理
        if (order != null && Constant.VALID_STATUS.equals(order.getStatus())) {
            String transactionId = UUID.randomUUID().toString();
            // 如果可以删除订单则发送消息给rocketmq，让用户中心消费消息
            
            rocketMQTemplate.sendMessageInTransaction("add-amount", MessageBuilder.withPayload(
                            UserAddMoneyDTO.builder().userCode(order.getAccountCode()).amount(order.getAmount()).build())
                    .setHeader(RocketMQHeaders.TRANSACTION_ID, transactionId).setHeader("order_id", order.getId())
                    .build(), order);
            
            //            changeStatus(order.getId(), CloudConstant.INVALID_STATUS);
        }
    }
}
