package com.tqz.alibaba.cloud.account.controller;

import com.tqz.alibaba.cloud.common.base.ResultData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 博客控制器。用于测试RestApi的权限问题。
 *
 * <p>如数据库存储的url都是 `/account/blog` ，但是某个用户的权限只有 {@link #get()} 接口对应的 `GET` 请求方式，
 * 没有 {@link #delete()} 接口对应 `DELETE` 请求方式。这种情况数据库可添加 `METHOD` 字段，用于表示请求方式，
 * 在做权限拦截的时候可根据url和请求方式两个信息拼接起来去判断。
 *
 * @author tianqingzhao
 * @since 2023/1/15 13:46
 */
@Slf4j
@RestController
@RequestMapping("/account/blog")
public class BlogController {
    
    /**
     * 该接口在数据库权限表存储对应的请求方式
     *
     * @return 响应内容
     */
    @GetMapping
    public ResultData<String> get() {
        log.info("请求获取博客信息");
        return ResultData.success("获取博客信息成功");
    }
    
    /**
     * 该接口不在数据库权限存储对应的请求方式
     *
     * @return 响应内容
     */
    @PostMapping
    public ResultData<String> delete() {
        log.info("删除博客信息");
        return ResultData.success("删除博客信息成功");
    }
}
