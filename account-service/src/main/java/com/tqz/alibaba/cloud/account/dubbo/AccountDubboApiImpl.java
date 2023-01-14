package com.tqz.alibaba.cloud.account.dubbo;

import com.tqz.alibaba.cloud.account.api.AccountDubboApi;
import com.tqz.alibaba.cloud.account.mapper.AccountMapper;
import com.tqz.alibaba.cloud.account.po.Account;
import com.tqz.alibaba.cloud.common.base.ResultData;
import com.tqz.alibaba.cloud.common.dto.AccountDTO;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * account服务对外提供dubbo接口。内部之间可使用dubbo，外部调用可使用feign。
 *
 * <p>来自官网的一段话：在Spring Cloud构建的微服务系统中，大多数的开发者使用都是官方提供的Feign组件来进行内部服务通信，
 * 这种声明式的HTTP客户端使用起来非常的简洁、方便、优雅，但是有一点，在使用Feign消费服务的时候，相比较Dubbo这种RPC框架而言，性能较差。
 * 虽说在微服务架构中，会讲按照业务划分的微服务独立部署，并且运行在各自的进程中。
 * 微服务之间的通信更加倾向于使用HTTP这种简答的通信机制，大多数情况都会使用REST API。
 * 这种通信方式非常的简洁高效，并且和开发平台、语言无关，但是通常情况下，HTTP并不会开启KeepAlive功能，
 * 即当前连接为短连接，短连接的缺点是每次请求都需要建立TCP连接，这使得其效率变的相当低下。
 * 对外部提供REST API服务是一件非常好的事情，但是如果内部调用也是使用HTTP调用方式，
 * 就会显得显得性能低下，Spring Cloud默认使用的Feign组件进行内部服务调用就是使用的HTTP协议进行调用，
 * 这时，我们如果内部服务使用RPC调用，对外使用REST API，将会是一个非常不错的选择，恰巧，Dubbo Spring Cloud给了我们这种选择的实现方式。
 *
 * @author tianqingzhao
 * @since 2023/1/12 10:27
 */
@Service
public class AccountDubboApiImpl implements AccountDubboApi {
    
    @Autowired
    private AccountMapper accountMapper;
    
    @Override
    public ResultData<AccountDTO> getByCode(String accountCode) {
        AccountDTO accountDTO = new AccountDTO();
        Account account = accountMapper.selectByCode(accountCode);
        BeanUtils.copyProperties(account, accountDTO);
        return ResultData.success(accountDTO);
    }
}
