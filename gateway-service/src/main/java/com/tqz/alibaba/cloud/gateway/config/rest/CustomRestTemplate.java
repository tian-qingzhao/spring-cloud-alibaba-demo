package com.tqz.alibaba.cloud.gateway.config.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Random;

/**
 * <p>自定义 RestTemplate 实现负载均衡。
 * 由于原生  {@link RestTemplate} 基于 {@link LoadBalanced} 实现的负载均衡是在 {@link SmartInitializingSingleton} 接口里面加工的，
 * 而 {@link SmartInitializingSingleton#afterSingletonsInstantiated()} 接口是在bean初始化完成之后执行的，
 * 如果服务在启动的时候在 {@link InitializingBean#afterPropertiesSet()} 方法需要通过 RestTemplate 根据服务名称调用其他某一个服务是调不通的，
 * 因为 {@link InitializingBean} 接口执行完之后才会执行 {@link SmartInitializingSingleton} 接口，
 * 所以服务启动的时候在 {@link InitializingBean} 接口使用 RestTemplate 还没有具备负载均衡的能力，需要自定义一个 RestTemplate。
 *
 * @author tianqingzhao
 * @since 2022/11/5 11:13
 */
@Slf4j
public class CustomRestTemplate extends RestTemplate {
    
    private final DiscoveryClient discoveryClient;
    
    public CustomRestTemplate(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }
    
    @Override
    protected <T> T doExecute(URI url, @Nullable HttpMethod method, @Nullable RequestCallback requestCallback,
            @Nullable ResponseExtractor<T> responseExtractor) throws RestClientException {
        
        Assert.notNull(url, "URI is required");
        Assert.notNull(method, "HttpMethod is required");
        ClientHttpResponse response = null;
        try {
            //判断url的拦截路径,然后去redis(作为注册中心)获取地址随机选取一个
            log.info("请求的url路径为:{}", url);
            url = replaceUrl(url);
            log.info("替换后的路径:{}", url);
            ClientHttpRequest request = createRequest(url, method);
            if (requestCallback != null) {
                requestCallback.doWithRequest(request);
            }
            response = request.execute();
            handleResponse(url, method, response);
            return (responseExtractor != null ? responseExtractor.extractData(response) : null);
        } catch (IOException ex) {
            String resource = url.toString();
            String query = url.getRawQuery();
            resource = (query != null ? resource.substring(0, resource.indexOf('?')) : resource);
            throw new ResourceAccessException(
                    "I/O error on " + method.name() + " request for \"" + resource + "\": " + ex.getMessage(), ex);
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }
    
    
    /**
     * 把服务实例名称替换为ip:端口
     *
     * @param url
     * @return
     */
    private URI replaceUrl(URI url) {
        //解析我们的微服务的名称
        String sourceUrl = url.toString();
        String[] httpUrl = sourceUrl.split("//");
        int index = httpUrl[1].replaceFirst("/", "@").indexOf("@");
        String serviceName = httpUrl[1].substring(0, index);
        
        //通过微服务的名称去nacos服务端获取 对应的实例列表
        List<ServiceInstance> serviceInstanceList = discoveryClient.getInstances(serviceName);
        if (serviceInstanceList.isEmpty()) {
            throw new RuntimeException("没有可用的微服务实例列表:" + serviceName);
        }
        
        //采取随机的获取一个
        Random random = new Random();
        Integer randomIndex = random.nextInt(serviceInstanceList.size());
        log.info("随机下标:{}", randomIndex);
        String serviceIp = serviceInstanceList.get(randomIndex).getUri().toString();
        log.info("随机选举的服务IP:{}", serviceIp);
        String targetSource = httpUrl[1].replace(serviceName, serviceIp);
        try {
            return new URI(targetSource);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return url;
    }
    
}
