package com.ruiji.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruiji.entity.Orders;
import org.springframework.stereotype.Service;

@Service
public interface OrdersService extends IService<Orders> {
    /**
     * 用户下单
     */
    public void submit(Orders orders);
}
