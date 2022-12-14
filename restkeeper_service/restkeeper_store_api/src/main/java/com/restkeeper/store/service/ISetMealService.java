package com.restkeeper.store.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.restkeeper.store.entity.SetMeal;
import com.restkeeper.store.entity.SetMealDish;

import java.util.List;

/**
 * @author MORRIS --> Java
 * @date 2022-12-14 23:52:10
 */
public interface ISetMealService extends IService<SetMeal> {
    //分页查询
    IPage<SetMeal> queryPage(int pageNum, int pageSize, String name);

    //添加套餐
    boolean add(SetMeal setmeal, List<SetMealDish> setMealDishes);

    //修改套餐
    boolean update(SetMeal setMeal, List<SetMealDish> setMealDishes);

    //停用套餐
    Boolean pauseSetMeal(String id);
}
