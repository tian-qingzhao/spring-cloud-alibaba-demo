package com.tqz.cloud.alibaba.component.openai.xunfei;

import cn.bugstack.openai.executor.model.xunfei.valobj.Choices;
import cn.bugstack.openai.executor.model.xunfei.valobj.Text;
import cn.bugstack.openai.executor.model.xunfei.valobj.XunFeiCompletionRequest;
import cn.bugstack.openai.executor.model.xunfei.valobj.XunFeiCompletionResponse;
import cn.bugstack.openai.executor.parameter.ChatChoice;
import cn.bugstack.openai.executor.parameter.CompletionRequest;
import cn.bugstack.openai.executor.parameter.CompletionResponse;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.jetbrains.annotations.NotNull;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * websocket监听器
 *
 * @author <a href="https://github.com/tian-qingzhao">tianqingzhao</a>
 * @since 2024/1/3 10:17
 */
@Slf4j
public class BigModelWebSocketListener extends WebSocketListener {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final XunFeiCompletionRequest request;
    private final EventSourceListener eventSourceListener;
    private final CountDownLatch countDownLatch = new CountDownLatch(1);
    private final EventSource eventSource;

    public BigModelWebSocketListener(XunFeiCompletionRequest request, EventSourceListener eventSourceListener) {
        this.request = request;
        this.eventSourceListener = eventSourceListener;
        this.eventSource = new EventSource() {

            @Override
            public Request request() {
                return this.request();
            }

            @Override
            public void cancel() {
                this.cancel();
            }
        };
    }

    @Override
    public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
        new Thread(() -> {
            try {
                String requestJsonStr = OBJECT_MAPPER.writeValueAsString(request);
                log.info("requestJsonStr：{}", requestJsonStr);

                webSocket.send(requestJsonStr);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            // 等待服务端返回完毕后关闭
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            webSocket.close(1000, "");
        }).start();
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        XunFeiCompletionResponse response = JSON.parseObject(text, XunFeiCompletionResponse.class);
        XunFeiCompletionResponse.Header header = response.getHeader();
        int code = header.getCode();

        // 反馈失败
        if (XunFeiCompletionResponse.Header.Code.SUCCESS.getValue() != code) {
            countDownLatch.countDown();
            return;
        }

        // 封装参数
        CompletionResponse completionResponse = new CompletionResponse();
        List<ChatChoice> chatChoices = new ArrayList<>();
        ChatChoice chatChoice = new ChatChoice();

        XunFeiCompletionResponse.Payload payload = response.getPayload();
        Choices choices = payload.getChoices();
        List<Text> texts = choices.getText();

        for (Text t : texts) {
            chatChoice.setDelta(cn.bugstack.openai.executor.parameter.Message.builder()
                    .name("")
                    .role(CompletionRequest.Role.SYSTEM)
                    .content(t.getContent())
                    .build());
            chatChoices.add(chatChoice);
        }
        completionResponse.setChoices(chatChoices);

        int status = header.getStatus();
        if (XunFeiCompletionResponse.Header.Status.START.getValue() == status) {
            eventSourceListener.onEvent(eventSource, null, null, JSON.toJSONString(completionResponse));
        } else if (XunFeiCompletionResponse.Header.Status.ING.getValue() == status) {
            eventSourceListener.onEvent(eventSource, null, null, JSON.toJSONString(completionResponse));
        } else if (XunFeiCompletionResponse.Header.Status.END.getValue() == status) {
            cn.bugstack.openai.executor.model.xunfei.valobj.Usage usage = payload.getUsage();
            cn.bugstack.openai.executor.model.xunfei.valobj.Usage.Text usageText = usage.getText();
            cn.bugstack.openai.executor.parameter.Usage openaiUsage = new cn.bugstack.openai.executor.parameter.Usage();
            openaiUsage.setPromptTokens(usageText.getPromptTokens());
            openaiUsage.setCompletionTokens(usageText.getCompletionTokens());
            openaiUsage.setTotalTokens(usageText.getTotalTokens());
            completionResponse.setUsage(openaiUsage);
            completionResponse.setCreated(System.currentTimeMillis());
            chatChoice.setFinishReason("stop");
            chatChoices.add(chatChoice);
            eventSourceListener.onEvent(eventSource, null, null, JSON.toJSONString(completionResponse));
            countDownLatch.countDown();
        }


    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        eventSourceListener.onClosed(eventSource);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
        eventSourceListener.onFailure(eventSource, t, response);
    }
}