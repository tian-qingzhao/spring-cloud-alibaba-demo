package com.tqz.alibaba.cloud.order.service;

import com.tqz.alibaba.cloud.common.base.ResultData;
import com.tqz.alibaba.cloud.common.dto.AccountDTO;
import com.tqz.alibaba.cloud.common.dto.OrderDTO;
import com.tqz.alibaba.cloud.order.vo.OrderVO;

/**
 * <p>
 * OrderService
 * </p>
 *
 * @author tianqingzhao
 * @since 2021/2/26 10:01
 */
public interface OrderService {
    
    /**
     * 下单接口
     *
     * @param orderDTO
     * @return
     */
    ResultData<OrderDTO> createOrder(OrderDTO orderDTO, String error);
    
    /**
     * 根据订单编号查询订单
     *
     * @param orderNo
     * @return
     */
    OrderDTO selectByNo(String orderNo);
    
    /**
     * 根据id改变状态
     *
     * @param id
     * @param status
     */
    void changeStatus(Integer id, String status);
    
    /**
     * 根据订单编码删除订单
     *
     * @param orderNo
     */
    void delete(String orderNo);
    
    /**
     * 根据RocketMQ收到信息之后，更改订单状态
     *
     * @param id            订单id
     * @param status        状态
     * @param transactionId 事物id
     */
    void changeStatuswithRocketMqLog(Integer id, String status, String transactionId);
    
    /**
     * 根据账号编码查询。该方法主要测试dubbo调用
     *
     * @param accountCode 账户编号
     * @return 账户信息
     */
    ResultData<AccountDTO> selectByAccountCode(String accountCode);
    
    /**
     * 根据账号编号和产品编号查询。该方法主要使用feign测试远程调用account服务和product服务。
     *
     * @param accountCode 账户编号
     * @param productCode 产品编号
     * @return 账户信息和产品信息
     */
    ResultData<OrderVO> selectByAccountCodeAndProductCode(String accountCode, String productCode);
    
    /**
     * 根据账号编号和产品编号查询。该方法主要使用dubbo测试远程调用account服务和product服务。
     *
     * @param accountCode 账户编号
     * @param productCode 产品编号
     * @return 账户信息和产品信息
     */
    ResultData<OrderVO> selectByAccountCodeAndProductCodeWithDubbo(String accountCode, String productCode);
}
