package com.tqz.alibaba.cloud.order.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *
 * @author tianqingzhao
 * @since 2022/8/11 8:33
 */
@RestController
public class SentinelTestController {
    
    @RequestMapping("sentinelTest")
    @SentinelResource(value = "sentinelTest", blockHandler = "handlerBlock")
    public String sentinelTest() {
        return System.currentTimeMillis() + "";
    }
    
    public String handlerBlock(BlockException e) {
        return "限流了...";
    }
}
