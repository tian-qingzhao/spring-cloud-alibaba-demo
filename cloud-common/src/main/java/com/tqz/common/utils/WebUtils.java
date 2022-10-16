package com.tqz.common.utils;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
}
