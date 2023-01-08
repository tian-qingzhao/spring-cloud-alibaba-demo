package com.tqz.alibaba.cloud.account.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * <p>
 *
 * @author tianqingzhao
 * @since 2022/12/28 15:41
 */
@Slf4j
public class EncoderPasswordTest {
    
    @Test
    public void test2() {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String rawPassword = "111111";
        
        String encode = passwordEncoder.encode(rawPassword);
        log.info("加密后的密码:" + encode);
        log.info("bcrypt密码对比:" + passwordEncoder.matches(rawPassword, encode));
        
        String md5Password = "{bcrypt}$2a$10$QYv6GeOLqzeacypYW0aCCOhE9iJCPyVTK3zB5PVxLbokP3VPt2/7S";
        log.info("MD5密码对比:" + passwordEncoder.matches(rawPassword, md5Password));
        
        // $2a$10$gExKdT3nkoFKfW1cFlqQUuFji3azHG.W4Pe3/WxHKANg3TpkSJRfW
        // $2a$10$BeIgP47AfRHYB4CEkMSDIuKNBVt/a1lBxaVEmLaBBgu9PEiN/kdyC
    }
}
