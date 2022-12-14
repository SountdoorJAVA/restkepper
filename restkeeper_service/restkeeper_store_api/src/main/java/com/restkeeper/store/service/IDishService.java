package com.restkeeper.store.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.restkeeper.store.entity.Dish;
import com.restkeeper.store.entity.DishFlavor;

import java.util.List;
import java.util.Map;

/**
 * @author MORRIS --> Java
 * @date 2022-12-14 19:25:52
 */
public interface IDishService extends IService<Dish> {
    //新增菜品
    boolean save(Dish dish, List<DishFlavor> flavorList);

    //修改菜品
    boolean update(Dish dish, List<DishFlavor> flavorList);

    //根据分类id和菜品name查询相关数据列表
    List<Map<String, Object>> findEnableDishListInfo(String categoryId, String name);
}
