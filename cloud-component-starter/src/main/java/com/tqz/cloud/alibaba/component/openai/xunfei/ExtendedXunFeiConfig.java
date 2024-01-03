package com.tqz.cloud.alibaba.component.openai.xunfei;

import cn.bugstack.openai.executor.model.xunfei.config.XunFeiConfig;
import lombok.Data;

/**
 * 扩展讯飞配置类
 *
 * @author tianqingzhao
 * @since 2024/1/3 9:32
 */
@Data
public class ExtendedXunFeiConfig extends XunFeiConfig {

    /**
     * 取值为[general,generalv2,generalv3]
     * 指定访问的领域,general指向V1.5版本,generalv2指向V2版本,generalv3指向V3版本 。
     * 注意：不同的取值对应的url也不一样！
     * 默认使用 general ，即v1.5版本接口
     *
     * <p>详见接口文档：<a href="https://www.xfyun.cn/doc/spark/Web.html"></a>
     */
    private String domain = "general";
}
