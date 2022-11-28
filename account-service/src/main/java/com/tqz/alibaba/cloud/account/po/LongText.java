package com.tqz.alibaba.cloud.account.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * <p>
 *
 * @author tianqingzhao
 * @since 2022/11/10 18:55
 */
@Data
@AllArgsConstructor
@TableName("t_long_text")
public class LongText {

    private Integer id;
    
    private String configStr;
    
}
