package com.ruiji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruiji.common.R;
import com.ruiji.dto.DishDto;
import com.ruiji.entity.Category;
import com.ruiji.entity.Dish;
import com.ruiji.entity.DishFlavor;
import com.ruiji.service.CategoryService;
import com.ruiji.service.DishFlavorService;
import com.ruiji.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 添加菜品信息功能
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.saveWithDishFlavor(dishDto);
       return R.success("添加成功");
    }
    /**
     * 菜品分页信息查询
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        log.info("page={},pageSize={},name={}",page,pageSize,name);
        //构造分页构造器
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();
        //构造条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper();
        //添加过滤条件
        queryWrapper.like(name!=null,Dish::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        //执行查询
        dishService.page(pageInfo,queryWrapper);
        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list=records.stream().map((item)->{
         DishDto dishDto = new DishDto();
         BeanUtils.copyProperties(item,dishDto);
         Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
         Category category = categoryService.getById(categoryId);
         if(category!=null) {
             String categoryName = category.getName();
             dishDto.setCategoryName(categoryName);
            }
        return dishDto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(list);
        //返回信息
        return R.success(dishDtoPage);
     }
     //修改菜品信息时回显
     @GetMapping("/{ids}")
     public R<DishDto> get(@PathVariable Long ids){
        DishDto disDto = dishService.getByIdWithDishFlavor(ids);
        return R.success(disDto);
    }
   //修改菜品信息后保存
    @PutMapping
       public R<String> update(@RequestBody DishDto dishDto){
           log.info(dishDto.toString());
           dishService.updateWithDishFlavor(dishDto);
          return R.success("修改成功");
       }
    /**
     * 对菜品批量或者是单个 进行停售或者是起售
     * @return
     */
    @PostMapping("/status/{status}")
//这个参数这里一定记得加注解才能获取到参数，否则这里非常容易出问题
    public R<String> status(@PathVariable("status") Integer status,@RequestParam List<Long> ids){
       dishService.updateStatus(status,ids);
        return R.success("售卖状态修改成功");
    }
    /**
     * 套餐批量删除和单个删除
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam("ids") List<Long> ids){
        //删除菜品  这里的删除是逻辑删除
        dishService.deleteByIds(ids);
        //删除菜品对应的口味  也是逻辑删除
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(DishFlavor::getDishId,ids);
        dishFlavorService.remove(queryWrapper);
        return R.success("菜品删除成功");
    }

    /**
     * 根据id查询套餐中菜品种类
     * @param dish
     * @return
     */
//    @GetMapping("/list")
//    public R<List<Dish>> list(Dish dish){
//      //条件构造器
//        LambdaQueryWrapper<Dish> QueryWrapper = new LambdaQueryWrapper<>();
//        //添加条件
//        QueryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
//        //状态必须为起售
//        QueryWrapper.eq(Dish::getStatus,1);
//        //添加排序条件
//        QueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//        List<Dish> list=dishService.list(QueryWrapper);
//        return R.success(list);
//    }
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        //条件构造器
        LambdaQueryWrapper<Dish> QueryWrapper = new LambdaQueryWrapper<>();
        //添加条件
        QueryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        //状态必须为起售
        QueryWrapper.eq(Dish::getStatus,1);
        //添加排序条件
        QueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list=dishService.list(QueryWrapper);
        List<DishDto> dishDtolist=list.stream().map((item)->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);
            if(category!=null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            //获取当前菜品id
            Long id = item.getId();
            LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DishFlavor::getDishId,id);
            List<DishFlavor> dishFlavorList = dishFlavorService.list(queryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());

        return R.success(dishDtolist);
    }
}
