package com.tqz.alibaba.cloud.gateway.config.loadbalancer;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.*;
import org.springframework.cloud.loadbalancer.core.NoopServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 灰度发布负载均衡策略。
 * 通过给请求头添加 Version 与 ServiceInstance 元数据属性进行对比。
 *
 * @author tianqingzhao
 * @since 2023/3/9 14:45
 */
@Log4j2
public class VersionLoadBalancer implements ReactorServiceInstanceLoadBalancer {

    private static final String VERSION_KEY = "version";

    private final ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;
    private final String serviceId;

    private final AtomicInteger position;

    public VersionLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider,
                               String serviceId) {
        this(serviceInstanceListSupplierProvider, serviceId, new Random().nextInt(1000));
    }

    public VersionLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider,
                               String serviceId, int seedPosition) {
        this.serviceId = serviceId;
        this.serviceInstanceListSupplierProvider = serviceInstanceListSupplierProvider;
        this.position = new AtomicInteger(seedPosition);
    }

    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        ServiceInstanceListSupplier supplier =
                this.serviceInstanceListSupplierProvider.getIfAvailable(NoopServiceInstanceListSupplier::new);

        return supplier.get(request).next()
                .map(serviceInstances -> processInstanceResponse(serviceInstances, request));
    }

    private Response<ServiceInstance> processInstanceResponse(List<ServiceInstance> instances,
                                                              Request<DefaultRequestContext> request) {
        if (instances.isEmpty()) {
            log.warn("No servers available for service: " + this.serviceId);
            return new EmptyResponse();
        }

        DefaultRequestContext requestContext = request.getContext();
        RequestData clientRequest = (RequestData) requestContext.getClientRequest();
        HttpHeaders headers = clientRequest.getHeaders();

        // get Request Header
        String reqVersion = headers.getFirst(VERSION_KEY);

        if (!StringUtils.hasLength(reqVersion)) {
            return processRibbonInstanceResponse(instances);
        }

        log.info("request header [version] : {}", reqVersion);
        // 请求的header里面的version如果与元数据配置的version匹配
        List<ServiceInstance> serviceInstances = instances.stream()
                .filter(instance -> reqVersion.equals(instance.getMetadata().get(VERSION_KEY)))
                .collect(Collectors.toList());

        if (serviceInstances.size() > 0) {
            return processRibbonInstanceResponse(serviceInstances);
        } else {
            return processRibbonInstanceResponse(instances);
        }
    }

    private Response<ServiceInstance> processRibbonInstanceResponse(List<ServiceInstance> instances) {
        int pos = Math.abs(this.position.incrementAndGet());
        ServiceInstance instance = instances.get(pos % instances.size());
        return new DefaultResponse(instance);
    }

}