package com.tqz.alibaba.cloud.account.controller;

import com.tqz.alibaba.cloud.common.base.ResultData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>测试隐私接口，也就是不能通过网关直接调用该接口，需要其他微服务使用Feign远程调用到该接口。
 *
 * @author tianqingzhao
 * @since 2023/3/9 14:17
 */
@RestController
public class SecretController {

    /**
     * 隐私接口，禁止通过网关访问
     */
    @GetMapping("/pv/account/getSecretValue")
    public ResultData<String> getSecretValue() {
        return ResultData.success("账户服务返回的数据：隐私接口，禁止通过网关访问");
    }
}
