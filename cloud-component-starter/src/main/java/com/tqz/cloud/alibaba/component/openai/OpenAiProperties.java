package com.tqz.cloud.alibaba.component.openai;

import cn.bugstack.openai.executor.model.aliyun.config.AliModelConfig;
import cn.bugstack.openai.executor.model.chatglm.config.ChatGLMConfig;
import cn.bugstack.openai.executor.model.chatgpt.config.ChatGPTConfig;
import cn.bugstack.openai.executor.model.xunfei.config.XunFeiConfig;
import com.tqz.cloud.alibaba.component.openai.xunfei.ExtendedXunFeiConfig;
import lombok.Data;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.time.Duration;

@Data
@ConfigurationProperties(prefix = OpenAiProperties.OPENAI_CONFIG_PREFIX)
public class OpenAiProperties {

    public final static String OPENAI_CONFIG_PREFIX = "openai.sdk.config";

    /**
     * 是否开启，默认为 <code>false</code>
     */
    private boolean enabled = false;

    /**
     * 智谱Ai ChatGLM Config
     */
    @NestedConfigurationProperty
    private ChatGLMConfig chatGlm;

    /**
     * OpenAi ChatGLM Config
     */
    @NestedConfigurationProperty
    private ChatGPTConfig chatGpt;

    /**
     * 讯飞
     */
    @NestedConfigurationProperty
    private XunFeiConfig xueFei;

    /**
     * 自己扩展的讯飞配置类
     */
    @NestedConfigurationProperty
    private ExtendedXunFeiConfig extendedXueFei;

    /**
     * 阿里通义千问
     */
    @NestedConfigurationProperty
    private AliModelConfig aliModel;


    /**
     * okhttp client config
     */
    private HttpLoggingInterceptor.Level level = HttpLoggingInterceptor.Level.HEADERS;

    /**
     * 连接超时时间
     */
    private Duration connectTimeout = Duration.ofSeconds(5L);

    /**
     * 写超时时间
     */
    private Duration writeTimeout = Duration.ofSeconds(5L);

    /**
     * 读超时时间
     */
    private Duration readTimeout = Duration.ofSeconds(5L);
}