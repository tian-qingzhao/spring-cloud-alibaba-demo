package com.tqz.cloud.alibaba.component.openai.xunfei;

import cn.bugstack.openai.executor.Executor;
import cn.bugstack.openai.executor.model.xunfei.utils.URLAuthUtils;
import cn.bugstack.openai.executor.model.xunfei.valobj.Chat;
import cn.bugstack.openai.executor.model.xunfei.valobj.Text;
import cn.bugstack.openai.executor.model.xunfei.valobj.XunFeiCompletionRequest;
import cn.bugstack.openai.executor.model.xunfei.valobj.XunFeiCompletionResponse;
import cn.bugstack.openai.executor.parameter.*;
import com.alibaba.fastjson.JSON;
import com.tqz.cloud.alibaba.component.openai.ExtendedConfiguration;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * 自定义扩展讯飞执行器
 *
 * @author tianqingzhao
 * @since 2024/1/2 15:23
 */
@Slf4j
public class ExtendedXunFeiModelExecutor implements Executor, ParameterHandler<XunFeiCompletionRequest> {

    /**
     * 配置信息
     */
    private final ExtendedXunFeiConfig xunFeiConfig;
    /**
     * 客户端
     */
    private final OkHttpClient okHttpClient;

    public ExtendedXunFeiModelExecutor(ExtendedConfiguration configuration) {
        this.xunFeiConfig = configuration.getExtendedXunFeiConfig();
        this.okHttpClient = configuration.getOkHttpClient();
    }

    @Override
    public EventSource completions(CompletionRequest completionRequest, EventSourceListener eventSourceListener) throws Exception {
        return completions(null, null, completionRequest, eventSourceListener);
    }

    @Override
    public EventSource completions(String apiHostByUser, String apiKeyByUser, CompletionRequest completionRequest, EventSourceListener eventSourceListener) throws Exception {
        // 1. 核心参数校验；不对用户的传参做更改，只返回错误信息。
        if (!completionRequest.isStream()) {
            throw new RuntimeException("illegal parameter stream is false!");
        }

        // 2. 动态设置 Host、Key，便于用户传递自己的信息
        String apiHost = null == apiHostByUser ? xunFeiConfig.getApiHost() : apiHostByUser;
        String authURL = getAuthURL(apiKeyByUser, apiHost);

        // 1. 转换参数信息
        XunFeiCompletionRequest xunFeiCompletionRequest = getParameterObject(completionRequest);
        // 2. 构建请求信息
        Request request = new Request.Builder()
                .url(authURL)
                .build();

        // 3. 调用请求
        WebSocket webSocket = okHttpClient.newWebSocket(request, new BigModelWebSocketListener(xunFeiCompletionRequest, eventSourceListener));
        // 4. 封装结果
        return new EventSource() {

            @NotNull
            @Override
            public Request request() {
                return request;
            }

            @Override
            public void cancel() {
                webSocket.cancel();
            }
        };
    }

    @Override
    public ImageResponse genImages(ImageRequest imageRequest) throws Exception {
        return genImages(null, null, imageRequest);
    }

    @Override
    public ImageResponse genImages(String apiHostByUser, String apiKeyByUser, ImageRequest imageRequest) throws Exception {

        // 1. 动态设置 Host、Key，便于用户传递自己的信息
        String apiHost = null == apiHostByUser ? xunFeiConfig.getApiTtiHost() : apiHostByUser;
        String authURL = getAuthURL(apiKeyByUser, apiHost, "POST", Boolean.FALSE);

        CompletionRequest completionRequest = CompletionRequest.builder()
                .messages(Collections.singletonList(Message.builder().role(CompletionRequest.Role.USER).content(imageRequest.getPrompt()).build()))
                .temperature(0.5)
                .build();
        // 1. 转换参数信息
        XunFeiCompletionRequest xunFeiCompletionRequest = getParameterObject(completionRequest, xunFeiConfig.getDomain());
        // 2. 构建请求信息
        Request request = new Request.Builder()
                .url(authURL)
                .post(RequestBody.create(MediaType.parse("POST"), JSON.toJSONString(xunFeiCompletionRequest)))
                .build();
        //3. 执行请求
        Response execute = okHttpClient.newCall(request).execute();
        if (execute.isSuccessful() && execute.body() != null) {
            XunFeiCompletionResponse xunFeiGenImageResponse = JSON.parseObject(execute.body().string(), XunFeiCompletionResponse.class);
            if (xunFeiGenImageResponse.getHeader().getCode() == XunFeiCompletionResponse.Header.Code.SUCCESS.getValue()) {
                XunFeiCompletionResponse.Payload payload = xunFeiGenImageResponse.getPayload();

                List<Text> texts = payload.getChoices().getText();
                ImageResponse imageResponse = new ImageResponse();
                imageResponse.setCreated(System.currentTimeMillis());
                imageResponse.setData(new ArrayList<>());
                for (Text text : texts) {
                    Item item = new Item();
                    item.setUrl(base64ToImageUrl(text.getContent()));
                    imageResponse.getData().add(item);
                }
                return imageResponse;

            } else {
                log.error("生成图片失败，code:{},message:{}", xunFeiGenImageResponse.getHeader().getCode(), xunFeiGenImageResponse.getHeader().getMessage());
            }
        }

        return null;
    }

    @Override
    public EventSource pictureUnderstanding(PictureRequest pictureRequest,
                                            EventSourceListener eventSourceListener) throws Exception {
        return pictureUnderstanding(null, null, pictureRequest, eventSourceListener);
    }

    @Override
    public EventSource pictureUnderstanding(String apiHostByUser,
                                            String apiKeyByUser,
                                            PictureRequest pictureRequest,
                                            EventSourceListener eventSourceListener) throws Exception {

        String apiHost = null == apiHostByUser ? xunFeiConfig.getApiPictureHost() : apiHostByUser;
        String authURL = getAuthURL(apiKeyByUser, apiHost);
        // 1. 转换参数信息
        XunFeiCompletionRequest xunFeiCompletionRequest = getParameterObject(pictureRequest);
        // 2. 构建请求信息
        Request request = new Request.Builder()
                .url(authURL)
                .build();

        WebSocket webSocket = okHttpClient.newWebSocket(request,
                new BigModelWebSocketListener(xunFeiCompletionRequest, eventSourceListener));

        return new EventSource() {
            @Override
            public Request request() {
                return request;
            }

            @Override
            public void cancel() {
                webSocket.cancel();
            }
        };
    }

    @Override
    public XunFeiCompletionRequest getParameterObject(CompletionRequest completionRequest) {
        return getParameterObject(completionRequest, xunFeiConfig.getDomain());
    }

    private String getAuthURL(String apiKeyByUser, String apiHost) throws Exception {

        return getAuthURL(apiKeyByUser, apiHost, "GET", Boolean.TRUE);
    }

    private String getAuthURL(String apiKeyByUser, String apiHost,
                              String httpMethod, Boolean websocket) throws Exception {
        String authURL;
        if (apiKeyByUser == null) {
            authURL = URLAuthUtils.getAuthURl(apiHost, xunFeiConfig.getApiKey(),
                    xunFeiConfig.getApiSecret(), httpMethod, websocket);
        } else {
            // 拆解 879d40fc.fe81b961ccb561c404f844838fa09876.MjUzYTdhMWEyNThiZDBhMTE1NmRjZTk3
            String[] configs = apiKeyByUser.split("\\.");
            authURL = URLAuthUtils.getAuthURl(apiHost, configs[1], configs[2], httpMethod, websocket);
        }
        return authURL;
    }

    private String base64ToImageUrl(String base64String) {
        // 将Base64编码字符串转换为字节数组
        byte[] imageBytes = Base64.getDecoder().decode(base64String.split(",")[1]);

        // 将字节数组转换为图像URL
        return "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(imageBytes);
    }

    private XunFeiCompletionRequest getParameterObject(CompletionRequest completionRequest, String domain) {
        // 头信息
        XunFeiCompletionRequest.Header header = getHeader();
        // 模型
        XunFeiCompletionRequest.Parameter parameter = XunFeiCompletionRequest.Parameter.builder().chat(
                        Chat.builder()
                                .domain(domain)
                                .temperature(completionRequest.getTemperature())
                                .maxTokens(completionRequest.getMaxTokens())
                                .build())
                .build();
        // 内容
        List<Text> texts = new ArrayList<>();
        List<cn.bugstack.openai.executor.parameter.Message> messages = completionRequest.getMessages();
        for (cn.bugstack.openai.executor.parameter.Message message : messages) {
            texts.add(Text.builder()
                    .role(Text.Role.USER.getName())
                    .content(message.getContent())
                    .build());
        }

        XunFeiCompletionRequest.Payload payload = XunFeiCompletionRequest.Payload.builder()
                .message(cn.bugstack.openai.executor.model.xunfei.valobj.Message.builder().text(texts).build())
                .build();

        return XunFeiCompletionRequest.builder()
                .header(header)
                .parameter(parameter)
                .payload(payload)
                .build();
    }

    private XunFeiCompletionRequest getParameterObject(PictureRequest pictureRequest) {
        // 头信息
        XunFeiCompletionRequest.Header header = getHeader();
        // 模型
        XunFeiCompletionRequest.Parameter parameter = XunFeiCompletionRequest.Parameter.builder().chat(
                        Chat.builder()
                                .domain(xunFeiConfig.getDomain())
                                .temperature(pictureRequest.getTemperature())
                                .maxTokens(pictureRequest.getMaxTokens())
                                .auditing("default")
                                .build())
                .build();
        // 内容
        List<Text> texts = new ArrayList<>();
        List<PictureRequest.Text> messages = pictureRequest.getMessages();
        for (PictureRequest.Text message : messages) {
            texts.add(Text.builder()
                    .role(Text.Role.USER.getName())
                    .content(message.getContent())
                    .contentType(message.getContentType())
                    .build());
        }

        XunFeiCompletionRequest.Payload payload = XunFeiCompletionRequest.Payload.builder()
                .message(cn.bugstack.openai.executor.model.xunfei.valobj.Message.builder().text(texts).build())
                .build();

        return XunFeiCompletionRequest.builder()
                .header(header)
                .parameter(parameter)
                .payload(payload)
                .build();
    }

    private XunFeiCompletionRequest.Header getHeader() {
        return XunFeiCompletionRequest.Header.builder()
                .appid(xunFeiConfig.getAppid())
                .uid(UUID.randomUUID().toString().substring(0, 10))
                .build();
    }
}
