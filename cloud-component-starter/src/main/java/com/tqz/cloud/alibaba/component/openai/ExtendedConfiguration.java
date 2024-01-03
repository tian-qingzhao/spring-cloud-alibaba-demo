package com.tqz.cloud.alibaba.component.openai;

import cn.bugstack.openai.executor.Executor;
import cn.bugstack.openai.executor.parameter.CompletionRequest;
import cn.bugstack.openai.session.Configuration;
import com.tqz.cloud.alibaba.component.openai.xunfei.ExtendedXunFeiConfig;
import com.tqz.cloud.alibaba.component.openai.xunfei.ExtendedXunFeiModelExecutor;
import lombok.Data;

import java.util.HashMap;

/**
 * 扩展配置类
 *
 * @author tianqingzhao
 * @since 2024/1/2 15:21
 */
@Data
public class ExtendedConfiguration extends Configuration {

    private ExtendedXunFeiConfig extendedXunFeiConfig;

    @Override
    public HashMap<String, Executor> newExecutorGroup() {
        HashMap<String, Executor> executorGroup = super.newExecutorGroup();

        Executor xunfeiModelExecutor = new ExtendedXunFeiModelExecutor(this);
        executorGroup.put(CompletionRequest.Model.XUNFEI.getCode(), xunfeiModelExecutor);

        return executorGroup;
    }
}
