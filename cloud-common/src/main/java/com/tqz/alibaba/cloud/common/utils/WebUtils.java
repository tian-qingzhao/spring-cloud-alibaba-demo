package com.tqz.alibaba.cloud.common.utils;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * <p>
 * WEB工具类
 * </p>
 *
 * @author tianqingzhao
 * @since 2021/2/25 9:59
 */
@Slf4j
public class WebUtils {
    
    private static final String IP_UTILS_FLAG = ",";
    
    private static final String UNKNOWN = "unknown";
    
    private static final String LOCALHOST_IP = "0:0:0:0:0:0:0:1";
    
    private static final String LOCALHOST_IP1 = "127.0.0.1";
    
    /**
     * 向客户端输出JSON字符串
     *
     * @param response HttpServletResponse
     * @param object   输出的数据
     */
    public static void writeJson(HttpServletResponse response, Object object) {
        writeData(response, JSON.toJSONString(object), MediaType.APPLICATION_JSON_VALUE);
    }
    
    /**
     * 客户端返回字符串
     *
     * @param response HttpServletResponse
     * @param data     需要返回的数据
     */
    public static void writeData(HttpServletResponse response, String data, String type) {
        try {
            response.setContentType(type);
            response.setCharacterEncoding("utf-8");
            response.getWriter().print(data);
            response.getWriter().flush();
            response.getWriter().close();
        } catch (IOException e) {
            log.error("writeData error", e);
        }
    }
    
    /**
     * 根据request获取客户端的ip地址。
     *
     * @param request 请求体
     * @return ip地址
     */
    public static String getIP(ServerHttpRequest request) {
        // 根据 HttpHeaders 获取 请求 IP地址
        String ip = request.getHeaders().getFirst("X-Forwarded-For");
        if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeaders().getFirst("x-forwarded-for");
            if (ip != null && ip.length() != 0 && !UNKNOWN.equalsIgnoreCase(ip)) {
                // 多次反向代理后会有多个ip值，第一个ip才是真实ip
                if (ip.contains(IP_UTILS_FLAG)) {
                    ip = ip.split(IP_UTILS_FLAG)[0];
                }
            }
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeaders().getFirst("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeaders().getFirst("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeaders().getFirst("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeaders().getFirst("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeaders().getFirst("X-Real-IP");
        }
        //兼容k8s集群获取ip
        if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddress().getAddress().getHostAddress();
            if (LOCALHOST_IP1.equalsIgnoreCase(ip) || LOCALHOST_IP.equalsIgnoreCase(ip)) {
                //根据网卡取本机配置的IP
                InetAddress iNet = null;
                try {
                    iNet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    log.error("getClientIp error: ", e);
                }
                ip = iNet.getHostAddress();
            }
        }
        return ip;
    }
    
}
