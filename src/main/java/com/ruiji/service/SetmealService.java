package com.ruiji.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruiji.dto.DishDto;
import com.ruiji.dto.SetmealDto;
import com.ruiji.entity.Setmeal;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SetmealService extends IService<Setmeal> {
    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     */
    public void saveWithDish(SetmealDto setmealDto);
    /**
     * 根据传过来的ids删除套餐
     */
    void deleteByIdsWithDish(List<Long> ids);
    /**
     * 批量或单个修改售卖状态
     */
    public void updateStatus(Integer status,List<Long> ids);
    /**
     * 回显套餐数据：根据套餐id查询套餐
     * @return
     */
    SetmealDto getDate(Long id);
}
