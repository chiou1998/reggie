package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.pojo.Category;
import com.itheima.reggie.pojo.Dish;
import com.itheima.reggie.pojo.Setmeal;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    @Override
    public void remove(Long id) {
        //查询当前分类是否关联了菜品

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getCategoryId,id);
        int count = dishService.count(queryWrapper);
        if (count>0) {
            //已经关联了菜品抛出一个异常;
            throw new CustomException("关联了菜品，无法删除");
        }

        //查询当前分类是否关联了套餐
        LambdaQueryWrapper<Setmeal> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(Setmeal::getCategoryId,id);
        int count1 = setmealService.count(queryWrapper1);
        if (count1>0) {
            //已经关联了菜品抛出一个异常;
            throw new CustomException("关联了套餐，无法删除");
        }
        //正常删除分类
        super.removeById(id);
    }
}
