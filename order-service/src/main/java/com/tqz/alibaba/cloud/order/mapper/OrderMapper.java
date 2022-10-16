package com.tqz.alibaba.cloud.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tqz.alibaba.cloud.order.po.Order;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * Account Dao层
 * </p>
 *
 * @author tianqingzhao
 * @since 2021/2/26 10:00
 */
public interface OrderMapper extends BaseMapper<Order> {

    /**
     * 根据订单编码获取订单
     *
     * @param orderNo 订单编号
     * @return
     */
    Order selectByNo(@Param("orderNo") String orderNo);

    /**
     * 修改订单状态
     *
     * @param id     订单主键id
     * @param status 状态
     */
    void changeStatus(@Param("id") Integer id, @Param("status") String status);
}
