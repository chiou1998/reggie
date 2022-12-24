package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.pojo.Dish;
import org.springframework.stereotype.Service;


public interface DishService extends IService<Dish> {
    //新增菜品同时插入菜品对应的口味数据，需要操作2张表
    public void saveWithFlavor(DishDto dishDto);


    public DishDto getByIdWithFlavor(Long id);

    void updateWithFlavor(DishDto dishDto);
}
