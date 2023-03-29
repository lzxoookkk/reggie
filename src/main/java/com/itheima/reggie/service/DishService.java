package com.itheima.reggie.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;

import org.springframework.web.bind.annotation.RequestBody;

public interface DishService extends IService<Dish> {

    void saveWithFlavor(@RequestBody DishDto dishDto);

    DishDto getByIdWithFlavor(Long id);

    void updateWithFlavor(DishDto dishDto);

}
