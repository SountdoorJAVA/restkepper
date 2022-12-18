package com.restkeeper.store.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.restkeeper.store.entity.Dish;
import com.restkeeper.store.entity.SetMealDish;
import com.restkeeper.store.mapper.SetMealDishMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

/**
 * @author MORRIS --> Java
 * @date 2022-12-15 00:39:53
 */
@Service(version = "1.0.0", protocol = "dubbo")
@org.springframework.stereotype.Service("setMealDishService")
public class SetMealDishServiceImpl extends ServiceImpl<SetMealDishMapper, SetMealDish> implements ISetMealDishService {
    @Autowired
    @Qualifier("dishService")
    private IDishService dishService;

    @Override
    public List<Dish> getAllDishBySetMealId(String setMealId) {
        List<String> dishIds = Lists.newArrayList();
        List<SetMealDish> setMealDishes = this.getBaseMapper().selectDishes(setMealId);
        setMealDishes.forEach(setMealDish -> {
            dishIds.add(setMealDish.getDishId());
        });

        return dishService.listByIds(dishIds);
    }

    //查询套餐中的菜品的份数
    @Override
    public Integer getDishCopiesInSetMeal(String dishId, String setMealId) {
        QueryWrapper<SetMealDish> qw = new QueryWrapper<>();
        qw.lambda().eq(SetMealDish::getDishId, dishId).eq(SetMealDish::getSetMealId, setMealId);
        SetMealDish dish = this.getOne(qw);
        return dish == null ? 0 : dish.getDishCopies();
    }
}
