package com.ruiji.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruiji.entity.ShoppingCart;
import com.ruiji.mapper.ShoppingCartMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ShoppingCartService extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements com.ruiji.service.ShoppingCartService {
}
