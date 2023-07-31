package com.ruiji.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruiji.entity.Dish;
import com.ruiji.entity.DishFlavor;
import com.ruiji.mapper.DishFlavorMapper;
import com.ruiji.service.DishFlavorService;
import com.ruiji.service.DishService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
