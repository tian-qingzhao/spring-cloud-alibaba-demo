package com.tqz.alibaba.cloud.account.listener;

import com.tqz.alibaba.cloud.account.mapper.AccountMapper;
import com.tqz.alibaba.cloud.common.dto.UserAddMoneyDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 账户服务添加余额，由订单服务扣减完订单之后，给对应的用户添加余额。
 *
 * @author tianqingzhao
 * @since 2021/3/1 9:49
 */
@Slf4j
@Service
@RocketMQMessageListener(topic = "${rocketmq.addAmountTopicName:}", consumerGroup = "${rocketmq.producer.group:}")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AddUserAmountListener implements RocketMQListener<UserAddMoneyDTO> {

    private final AccountMapper accountMapper;

    /**
     * 收到消息的业务逻辑
     *
     * @param userAddMoneyDTO 消息對象
     */
    @Override
    public void onMessage(UserAddMoneyDTO userAddMoneyDTO) {
        log.info("received message: {}", userAddMoneyDTO);
        //todo 给用户增加总额
        accountMapper.increaseAmount(userAddMoneyDTO.getUserCode(), userAddMoneyDTO.getAmount());
        log.info("add money success");
    }
}
