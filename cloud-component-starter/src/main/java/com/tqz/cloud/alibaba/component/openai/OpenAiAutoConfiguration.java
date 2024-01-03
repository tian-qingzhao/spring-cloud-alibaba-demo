package com.tqz.cloud.alibaba.component.openai;

import cn.bugstack.openai.session.OpenAiSession;
import cn.bugstack.openai.session.OpenAiSessionFactory;
import cn.bugstack.openai.session.defaults.DefaultOpenAiSessionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = OpenAiProperties.OPENAI_CONFIG_PREFIX, name = "enabled", havingValue = "true")
@EnableConfigurationProperties(OpenAiProperties.class)
public class OpenAiAutoConfiguration {

    @Bean
    public OpenAiSession openAiSession(OpenAiSessionFactory openAiSessionFactory) {
        return openAiSessionFactory.openSession();
    }

    @Bean
    public OpenAiSessionFactory openAiSessionFactory(OpenAiProperties openAiProperties) {
        ExtendedConfiguration configuration = new ExtendedConfiguration();

        configuration.setAliModelConfig(openAiProperties.getAliModel());
        configuration.setChatGLMConfig(openAiProperties.getChatGlm());
        configuration.setChatGPTConfig(openAiProperties.getChatGpt());
        configuration.setXunFeiConfig(openAiProperties.getXueFei());
        configuration.setExtendedXunFeiConfig(openAiProperties.getExtendedXueFei());

        //okHttpClient Config
        configuration.setLevel(openAiProperties.getLevel());
        configuration.setReadTimeout(openAiProperties.getReadTimeout().getSeconds());
        configuration.setWriteTimeout(openAiProperties.getWriteTimeout().getSeconds());
        configuration.setConnectTimeout(openAiProperties.getConnectTimeout().getSeconds());
        return new DefaultOpenAiSessionFactory(configuration);
    }
}