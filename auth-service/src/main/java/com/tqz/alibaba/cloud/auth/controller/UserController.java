package com.tqz.alibaba.cloud.auth.controller;

import com.tqz.alibaba.cloud.auth.mapper.SysUserMapper;
import com.tqz.alibaba.cloud.auth.po.SysUser;
import com.tqz.alibaba.cloud.auth.service.ISysUserService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * <p>
 * 用户接口
 * </p>
 *
 * @author tianqingzhao
 * @since 2021/3/6 15:14
 */
@Api(value = "用户接口")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserController {
    
    public final ISysUserService sysUserService;
    
    public final SysUserMapper userMapper;
    
    @GetMapping("getByName")
    public SysUser getByName(@RequestParam(defaultValue = "tqz") String username) {
        return userMapper.selectByUserName(username);
    }
    
    /**
     * 获取授权的用户信息。
     *
     * <p>用了透明令牌jwt token后资源服务器可以直接解析验证token，
     * 资源管理器(account-service/product-service/order-service)不再需要调用认证服务器获取用户。
     *
     * @param principal 当前用户
     * @return 授权信息
     */
    @GetMapping("current/get")
    public Principal user(Principal principal) {
        return principal;
    }
}
