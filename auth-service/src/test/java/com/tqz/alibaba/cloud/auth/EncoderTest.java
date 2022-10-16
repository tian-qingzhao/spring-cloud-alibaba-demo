package com.tqz.alibaba.cloud.auth;

import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * <p>
 *
 * @author tianqingzhao
 * @since 2022/8/12 13:46
 */
public class EncoderTest {
    
    @Test
    public void testEncoder() {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        
        String password = "account-app";
        String encoderPwd1 = encoder.encode(password);
        String encoderPwd2 = encoder.encode(password);
        
        System.out.println("encoderPwd1:" + encoderPwd1);
        System.out.println("encoderPwd1:" + encoderPwd2);
        
        System.out.println(encoder.matches(password, encoderPwd1));
        System.out.println(encoder.matches(password, encoderPwd2));
    }
}
