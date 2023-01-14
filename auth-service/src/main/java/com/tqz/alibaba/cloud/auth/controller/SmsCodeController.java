package com.tqz.alibaba.cloud.auth.controller;

import com.tqz.alibaba.cloud.auth.po.Pair;
import com.tqz.alibaba.cloud.auth.sms.InMemorySmsCodeCacheManager;
import com.tqz.alibaba.cloud.common.base.ResultData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>根据手机号获取验证码控制器
 *
 * @author tianqingzhao
 * @since 2023/1/10 9:26
 */
@Slf4j
@RestController
@RequestMapping("/smsCode")
public class SmsCodeController {
    
    @Autowired
    private InMemorySmsCodeCacheManager smsCodeCacheManager;
    
    @RequestMapping("/getSmsCode")
    public ResultData<String> getSmsCode(String mobile) {
        if (StringUtils.isEmpty(mobile)) {
            return ResultData.fail("手机号不能为空");
        }
        
        Pair<Long, String> pair = smsCodeCacheManager.getIfNotExitsGenerator(mobile);
        String smsCode = pair.getValue();
        
        log.info("手机号 {} 获取的验证码 {}", mobile, smsCode);
        
        return ResultData.success(smsCode);
    }
}
