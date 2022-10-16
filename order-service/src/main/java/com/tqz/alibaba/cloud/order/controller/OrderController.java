package com.tqz.alibaba.cloud.order.controller;

import com.tqz.alibaba.cloud.common.base.ResultData;
import com.tqz.alibaba.cloud.order.dto.OrderDTO;
import com.tqz.alibaba.cloud.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * <p>
 * order控制器
 * </p>
 *
 * @author tianqingzhao
 * @since 2021/2/26 9:59
 */
@RestController
@RequestMapping("order")
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired) )
public class OrderController {
    
    private final OrderService orderService;

    @PostMapping("/create")
    public ResultData<OrderDTO> create(OrderDTO orderDTO){
        log.info("create order:{}",orderDTO);
        return orderService.createOrder(orderDTO);
    }


    /**
     * 根据订单号删除订单
     * @param orderNo 订单编号
     */
    @PostMapping("/delete")
    public ResultData<String> delete(@RequestParam String orderNo){
        log.info("delete order id is {}",orderNo);
        orderService.delete(orderNo);
        return ResultData.success("订单删除成功");
    }


}
