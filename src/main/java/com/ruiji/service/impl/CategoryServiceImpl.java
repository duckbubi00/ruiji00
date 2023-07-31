package com.ruiji.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruiji.common.CustomException;
import com.ruiji.entity.Category;
import com.ruiji.entity.Dish;
import com.ruiji.entity.Setmeal;
import com.ruiji.mapper.CategoryMapper;
import com.ruiji.service.CategoryService;
import com.ruiji.service.DishService;
import com.ruiji.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper,Category> implements CategoryService {
    /**
     * 根据id删除分类，删除前先判断条件
     * @param id
     */
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;
    @Override
    public void remove(Long ids) {
        LambdaQueryWrapper<Dish> LambdaQueryWrapper = new LambdaQueryWrapper<>();
         //查询条件
          LambdaQueryWrapper.eq(Dish::getCategoryId,ids);
          int count = dishService.count(LambdaQueryWrapper);
         //查询当前分类是否关联了菜品，如果已经关联，抛出一个业务异常
         if (count>0){
         throw new CustomException("分类关联菜品，不可删除");
        }
        //查询当前分类是否关联套餐，如果关联则抛出异常
        LambdaQueryWrapper<Setmeal> LambdaQueryWrapper1 = new LambdaQueryWrapper<>();
        //查询条件
        LambdaQueryWrapper1.eq(Setmeal::getCategoryId,ids);
        int count1 = setmealService.count(LambdaQueryWrapper1);
        if(count1>0){
         throw  new CustomException("分类关联套餐，不可删除");
        }
        //    正常删除分癸
     super.removeById(ids);
    }
}
