package com.ruiji.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruiji.dto.DishDto;
import com.ruiji.entity.Dish;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DishService extends IService<Dish> {
    public void saveWithDishFlavor(DishDto dishDto);

    /**
     * 根据id查询对应的菜品信息和菜品口味
     * @param ids
     */
    public DishDto getByIdWithDishFlavor(Long ids);
    /**
     * 更新菜品信息，同时更新口味信息
     */
    public void  updateWithDishFlavor(DishDto dishDto);
    /**
     *  根据传过来的id批量或者是单个的删除菜品
     */
    void deleteByIds(List<Long> ids);
    /**
     * 批量或单个修改售卖状态
     */
    public void updateStatus(Integer status,List<Long> ids);
}
