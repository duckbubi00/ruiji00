package com.ruiji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruiji.common.R;
import com.ruiji.entity.Category;
import com.ruiji.entity.Employee;
import com.ruiji.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {
 @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info("Category:{}",category);
    categoryService.save(category);
    return R.success("新增分类成功");
}

    /**
     * 分页展示功能
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize){
      //分页构造器
        Page<Category> pageInfo = new Page<>(page, pageSize);
        //条件构造器
    LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
    //添加排序条件，根据sort进行排序
    queryWrapper.orderByDesc(Category::getSort);
    //进行分页查询
    categoryService.page(pageInfo,queryWrapper);
    return R.success(pageInfo);
}

    /**
     * 根据id删除分类
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long ids){
        log.info("删除分类，ids为：{}",ids);
        categoryService.remove(ids);
        return R.success("分类信息删除成功");
    }

    /**
     * 修改分类数据
     * @param request
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Category category){
        log.info(category.toString());
        categoryService.updateById(category);
        return R.success("分类信息修改成功");
    }

    /**
     * 根据条件查询到分类数据
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
//条件构造器
        LambdaQueryWrapper<Category> QueryWrapper = new LambdaQueryWrapper<>();
        //添加条件
        QueryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
        //添加排序条件
        QueryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list=categoryService.list(QueryWrapper);
        return R.success(list);
    }

}
