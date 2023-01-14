package com.tqz.alibaba.cloud.order.controller;

import com.tqz.alibaba.cloud.common.base.ResultData;
import com.tqz.alibaba.cloud.common.dto.AccountDTO;
import com.tqz.alibaba.cloud.common.dto.OrderDTO;
import com.tqz.alibaba.cloud.order.service.OrderService;
import com.tqz.alibaba.cloud.order.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrderController {
    
    private final OrderService orderService;
    
    @GetMapping("selectByAccountCode")
    public ResultData<AccountDTO> selectByAccountCode(@RequestParam(defaultValue = "tian") String accountCode) {
        return orderService.selectByAccountCode(accountCode);
    }
    
    @GetMapping("selectByAccountCodeAndProductCode")
    public ResultData<OrderVO> selectByAccountCodeAndProductCode(
            @RequestParam(defaultValue = "tian") String accountCode,
            @RequestParam(defaultValue = "001") String productCode) {
        return orderService.selectByAccountCodeAndProductCode(accountCode, productCode);
    }
    
    @GetMapping("selectByAccountCodeAndProductCodeWithDubbo")
    public ResultData<OrderVO> selectByAccountCodeAndProductCodeWithDubbo(
            @RequestParam(defaultValue = "tian") String accountCode,
            @RequestParam(defaultValue = "001") String productCode) {
        return orderService.selectByAccountCodeAndProductCodeWithDubbo(accountCode, productCode);
    }
    
    @PostMapping("/create")
    public ResultData<OrderDTO> create(OrderDTO orderDTO, @RequestParam(defaultValue = "success") String error) {
        log.info("create order:{}, error:{}", orderDTO, error);
        return orderService.createOrder(orderDTO, error);
    }
    
    
    /**
     * 根据订单号删除订单
     *
     * @param orderNo 订单编号
     */
    @PostMapping("/delete")
    public ResultData<String> delete(@RequestParam String orderNo) {
        log.info("delete order id is {}", orderNo);
        orderService.delete(orderNo);
        return ResultData.success("订单删除成功");
    }
    
    
}
