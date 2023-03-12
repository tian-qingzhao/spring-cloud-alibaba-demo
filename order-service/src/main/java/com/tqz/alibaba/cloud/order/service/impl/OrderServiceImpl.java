package com.tqz.alibaba.cloud.order.service.impl;

import com.tqz.alibaba.cloud.account.api.AccountDubboApi;
import com.tqz.alibaba.cloud.common.base.Constant;
import com.tqz.alibaba.cloud.common.base.ResultData;
import com.tqz.alibaba.cloud.common.base.ReturnCode;
import com.tqz.alibaba.cloud.common.dto.AccountDTO;
import com.tqz.alibaba.cloud.common.dto.OrderDTO;
import com.tqz.alibaba.cloud.common.dto.ProductDTO;
import com.tqz.alibaba.cloud.common.dto.UserAddMoneyDTO;
import com.tqz.alibaba.cloud.common.exception.ServiceException;
import com.tqz.alibaba.cloud.order.feign.AccountFeignClient;
import com.tqz.alibaba.cloud.order.feign.ProductFeignClient;
import com.tqz.alibaba.cloud.order.listener.DeleteOrderListener;
import com.tqz.alibaba.cloud.order.mapper.OrderMapper;
import com.tqz.alibaba.cloud.order.mapper.RocketMqTransactionLogMapper;
import com.tqz.alibaba.cloud.order.po.Order;
import com.tqz.alibaba.cloud.order.po.RocketmqTransactionLog;
import com.tqz.alibaba.cloud.order.service.OrderService;
import com.tqz.alibaba.cloud.order.vo.OrderVO;
import com.tqz.alibaba.cloud.product.api.ProductDubboApi;
import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
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

    @Reference(interfaceName = "com.tqz.alibaba.cloud.account.api.AccountDubboApi", generic = true)
    private AccountDubboApi accountDubboApi;

    @Reference(interfaceName = "com.tqz.alibaba.cloud.product.api.ProductDubboApi", generic = true)
    private ProductDubboApi productDubboApi;

    @Value(("${rocketmq.addAmountTopicName}"))
    private String addAmountTopicName;

    private static final String ERROR = "error";

    @GlobalTransactional(name = "TX_ORDER_CREATE")
    @Override
    public ResultData<OrderDTO> createOrder(OrderDTO orderDTO, String error) {
        orderDTO.setOrderNo(UUID.randomUUID().toString());

        log.info("ORDER XID is: {}", RootContext.getXID());

        ResultData<BigDecimal> productResult = productFeignClient.deduct(orderDTO.getProductCode(),
                orderDTO.getCount());

        if (ERROR.equals(error)) {
            throw new RuntimeException("下订单接口故意抛出的异常，测试分布式事务");
        }

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

    @Override
    public OrderDTO selectByNo(String orderNo) {
        OrderDTO orderDTO = new OrderDTO();
        Order order = orderMapper.selectByNo(orderNo);
        BeanUtils.copyProperties(order, orderDTO);
        return orderDTO;
    }

    @Override
    public ResultData<String> delete(String orderNo) {
        Order order = orderMapper.selectByNo(orderNo);
        if (order == null) {
            return ResultData.fail(String.format("订单 %s 不存在", orderNo));
        }
        if (!Constant.VALID_STATUS.equals(order.getStatus())) {
            return ResultData.fail(String.format("订单 %s 状态无效，不能删除", orderNo));
        }

        // 如果订单存在且状态为有效，进行业务处理
        String transactionId = UUID.randomUUID().toString();
        UserAddMoneyDTO userAddMoneyDTO = UserAddMoneyDTO.builder()
                .userCode(order.getAccountCode())
                .amount(order.getAmount())
                .build();

        // 如果可以删除订单则发送消息给rocketmq，让用户中心消费消息
        TransactionSendResult sendResult = rocketMQTemplate.sendMessageInTransaction(addAmountTopicName,
                MessageBuilder.withPayload(userAddMoneyDTO)
                        .setHeader(RocketMQHeaders.TRANSACTION_ID, transactionId)
                        .setHeader(DeleteOrderListener.ORDER_ID_KEY, order.getId())
                        .build(), order);

        if (LocalTransactionState.COMMIT_MESSAGE == sendResult.getLocalTransactionState()) {
            return ResultData.success("删除成功");
        } else {
            return ResultData.fail("删除失败");
        }
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public void changeStatuswithRocketMqLog(Integer id, String status, String transactionId) {
        int count = orderMapper.changeStatus(id, status);
        if (count > 0) {
            rocketMqTransactionLogMapper.insert(
                    RocketmqTransactionLog.builder().transactionId(transactionId).log("执行删除订单操作").build());
        }
    }

    @Override
    public ResultData<AccountDTO> selectByAccountCode(String accountCode) {
        log.info("order-service使用dubbo调用account-service，请求参数accountCode：{}", accountCode);

        // dubbo调用,线程数：1000，Ramp-Up：10，平均时长：23ms，最小时长：16ms，最大时长：188ms，吞吐量：96.4/s
        return accountDubboApi.getByCode(accountCode);

        // feign调用,线程数：1000，Ramp-Up：10，平均时长：714ms，最小时长：22ms，最大时长：3060ms，吞吐量：87.7/s
        // return accountFeignClient.getByCode(accountCode);
    }

    @Override
    public ResultData<OrderVO> selectByAccountCodeAndProductCode(String accountCode, String productCode) {
        log.info("order服务使用feign远程调用account服务和product服务");

        ResultData<AccountDTO> accountResult = accountFeignClient.getByCode(accountCode);

        ResultData<ProductDTO> productResult = productFeignClient.getByCode(productCode);

        OrderVO orderVO = setOrderVO(accountResult, productResult);

        return ResultData.success(orderVO);
    }

    @Override
    public ResultData<OrderVO> selectByAccountCodeAndProductCodeWithDubbo(String accountCode, String productCode) {
        log.info("order服务使用dubbo远程调用account服务和product服务");

        ResultData<AccountDTO> accountResult = accountDubboApi.getByCode(accountCode);

        ResultData<ProductDTO> productResult = productDubboApi.getByCode(productCode);

        OrderVO orderVO = setOrderVO(accountResult, productResult);

        return ResultData.success(orderVO);
    }

    @Override
    public ResultData<String> getSecretValue() {
        return accountFeignClient.getSecretValue();
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public void saveOrder(Order order) {
        orderMapper.insert(order);
    }

    private OrderVO setOrderVO(ResultData<AccountDTO> accountResult, ResultData<ProductDTO> productResult) {
        OrderVO orderVO = new OrderVO();

        Optional<AccountDTO> accountDTO = Optional.ofNullable(accountResult.getData());
        AccountDTO account = accountDTO.orElse(new AccountDTO());
        log.info("查询账户信息成功：{}", account);

        Optional<ProductDTO> productDTO = Optional.ofNullable(productResult.getData());
        ProductDTO product = productDTO.orElse(new ProductDTO());
        log.info("查询产品信息成功：{}", product);

        orderVO.setAccountDTO(account);
        orderVO.setProductDTO(product);

        return orderVO;
    }

}
