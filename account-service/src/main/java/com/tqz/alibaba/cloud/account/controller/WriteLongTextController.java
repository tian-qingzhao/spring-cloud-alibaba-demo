package com.tqz.alibaba.cloud.account.controller;

import com.tqz.alibaba.cloud.account.mapper.LongText2Mapper;
import com.tqz.alibaba.cloud.account.mapper.LongTextMapper;
import com.tqz.alibaba.cloud.account.po.LongText2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileInputStream;
import java.util.UUID;

/**
 * <p>
 *
 * @author tianqingzhao
 * @since 2022/11/10 18:55
 */
@RestController
public class WriteLongTextController {
    
    @Autowired
    private LongTextMapper longTextMapper;
    
    @Autowired
    private LongText2Mapper longText2Mapper;
    
    @RequestMapping("write")
    public String write() {
        String configStr = readConfigStr();
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < 5000; i++) {
            String configId = UUID.randomUUID().toString();
            longText2Mapper.insert(new LongText2(configId, configStr));
        }
        
        long endTime = System.currentTimeMillis();
        return (endTime - startTime) + "";
    }
    
    private static String readConfigStr() {
        try {
            FileInputStream fileInputStream = new FileInputStream("E:/long_text.txt");
            
            byte[] buf = new byte[102400];
            int len = 0;
            String str = "";
            while ((len = fileInputStream.read(buf)) != -1) {
                str = new String(buf);
            }
            return str;
        } catch (Exception e) {
            return null;
        }
    }
    
    public static void main(String[] args) {
        //        System.out.println(readConfigStr());
        System.out.println(UUID.randomUUID().toString());
    }
}
