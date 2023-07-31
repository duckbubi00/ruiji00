package com.ruiji.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruiji.common.CustomException;
import com.ruiji.dto.DishDto;
import com.ruiji.dto.SetmealDto;
import com.ruiji.entity.Dish;
import com.ruiji.entity.DishFlavor;
import com.ruiji.entity.Setmeal;
import com.ruiji.entity.SetmealDish;
import com.ruiji.mapper.SetmealMapper;
import com.ruiji.service.SetmealDishService;
import com.ruiji.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private SetmealService setmealService;
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
       // 保存套餐的基本信息，操作setmeal，执行insert操作
        this.save(setmealDto);
        //保存套餐和菜品的关联信息，操作setmeal_dish,执行insert操作
        List<SetmealDish> SetmealDishs = setmealDto.getSetmealDishes();
        SetmealDishs = SetmealDishs.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(SetmealDishs);
    }

    /**
     * 删除套餐方法
     * @param ids
     */
    @Override
    @Transactional
    public void deleteByIdsWithDish(List<Long> ids) {
        //构造条件查询器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //先查询该套餐是否在售卖，如果是则抛出业务异常
        queryWrapper.in(ids != null, Setmeal::getId, ids);
        List<Setmeal> list = this.list(queryWrapper);
        for (Setmeal setmeal : list) {
            Integer status = setmeal.getStatus();
            //如果不是在售卖,则可以删除
            if (status == 0) {
                this.removeById(setmeal.getId());
            } else {
                //此时应该回滚,因为可能前面的删除了，但是后面的是正在售卖
                throw new CustomException("删除套餐中有正在售卖菜品,无法全部删除");
            }
        }
    }

    /**
     * 批量修改套餐售卖状态
     * @param status
     * @param ids
     */
    @Override
    @Transactional
    public void updateStatus(Integer status, List<Long> ids) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.in(ids != null, Setmeal::getId, ids);
        //根据数据进行批量查询
        List<Setmeal> list = setmealService.list(queryWrapper);
        for (Setmeal setmeal : list) {
            if (setmeal != null) {
                setmeal.setStatus(status);
                setmealService.updateById(setmeal);
            }
        }
    }

    /**
     * 回显套餐数据：根据套餐id查询套餐
     * @return
     */
    @Override
    public SetmealDto getDate(Long id) {
        Setmeal setmeal = this.getById(id);
        SetmealDto setmealDto = new SetmealDto();
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper();
        //在关联表中查询，setmealdish
        queryWrapper.eq(id!=null,SetmealDish::getSetmealId,id);

        if (setmeal != null){
            BeanUtils.copyProperties(setmeal,setmealDto);
            List<SetmealDish> list = setmealDishService.list(queryWrapper);
            setmealDto.setSetmealDishes(list);
            return setmealDto;
        }
        return null;
    }
    }
