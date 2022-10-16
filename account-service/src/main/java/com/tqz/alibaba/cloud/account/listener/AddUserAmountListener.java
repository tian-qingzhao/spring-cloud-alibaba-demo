package com.tqz.alibaba.cloud.account.listener;/*
package com.tqz.account.listener;

import com.tqz.account.mapper.AccountMapper;
import com.tqz.product.message.UserAddMoneyDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

*/
/**
 * <p>
 *
 * </p>
 *
 * @author tianqingzhao
 * @since 2021/3/1 9:49
 *//*

@Slf4j
@Service
@RocketMQMessageListener(topic = "add-amount", consumerGroup = "cloud-group")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AddUserAmountListener implements RocketMQListener<UserAddMoneyDTO> {
    private final AccountMapper accountMapper;

    */
/**
     * 收到消息的业务逻辑
     *
     * @param userAddMoneyDTO 消息對象
     *//*

    @Override
    public void onMessage(UserAddMoneyDTO userAddMoneyDTO) {
        log.info("received message: {}", userAddMoneyDTO);
        //todo 给用户增加总额
        accountMapper.increaseAmount(userAddMoneyDTO.getUserCode(), userAddMoneyDTO.getAmount());
        log.info("add money success");
    }
}
*/
