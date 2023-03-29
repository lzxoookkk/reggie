package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> save(@RequestBody Category category){
        categoryService.save(category);
        return R.success("新增菜品成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize){

        Page<Category> pageInfo = new Page<Category>(page,pageSize);

        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper();
        lqw.orderByAsc(Category::getSort);

        categoryService.page(pageInfo,lqw);

        return R.success(pageInfo);
    }

    @DeleteMapping
    public R<String> delete(Long id){

        categoryService.remove(id);
        return R.success("删除成功");

    }

    @PutMapping
    public R<String> update(@RequestBody Category category){

        categoryService.updateById(category);
        return R.success("修改成功");

    }

    @GetMapping("/list")
    public R<List<Category>> list (Category category){

        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
        lqw.eq(category.getType() != null,Category::getType,category.getType());
        lqw.orderByDesc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list = categoryService.list(lqw);

        return R.success(list);
    }
}
