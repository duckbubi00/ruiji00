package com.ruiji.controller;

import com.ruiji.common.R;
import com.ruiji.entity.OrderDetail;
import com.ruiji.entity.Orders;
import com.ruiji.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/order")
public class OrdersController {
    @Autowired
    private OrdersService ordersService;
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info("订单数据{}",orders);
        ordersService.submit(orders);
        return R.success("下单成功");
    }

}
