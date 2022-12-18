package com.restkeeper.store.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.restkeeper.store.entity.Dish;
import com.restkeeper.store.entity.SetMealDish;

import java.util.List;

/**
 * @author MORRIS --> Java
 * @date 2022-12-15 00:39:18
 */
public interface ISetMealDishService extends IService<SetMealDish> {
    List<Dish> getAllDishBySetMealId(String dishId);

    Integer getDishCopiesInSetMeal(String dishId, String setMealId);
}
