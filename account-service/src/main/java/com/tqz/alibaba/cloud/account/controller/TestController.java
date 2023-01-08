package com.tqz.alibaba.cloud.account.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *
 * @autoor tianqingzhao
 * @since 2021/7/4 16:35
 */
@Slf4j
@RestController
public class TestController {

    @SentinelResource(value = "/customDataPersistence",blockHandler = "handlerCustomDataPersistenceException")
    @GetMapping("customDataPersistence")
    public String getByCode(){
        return "tianqingzhao";
    }

    public String handlerCustomDataPersistenceException(BlockException blockException) {
        log.info(blockException.getMessage());
        return "被限流了！";
    }
}
