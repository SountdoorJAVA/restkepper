package com.restkeeper.store.service;

import com.alibaba.nacos.client.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restkeeper.constants.SystemCode;
import com.restkeeper.store.entity.Dish;
import com.restkeeper.store.entity.DishFlavor;
import com.restkeeper.store.mapper.DishMapper;
import com.restkeeper.utils.Result;
import lombok.val;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @author MORRIS --> Java
 * @date 2022-12-14 19:26:35
 */
@Service(version = "1.0.0", protocol = "dubbo")
@org.springframework.stereotype.Service("dishService")
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements IDishService {
    @Autowired
    @Qualifier("dishFlavorService")
    private IDishFlavorService dishFlavorService;

    //保存菜品(菜品表,口味表,一对多的关系)
    @Override
    @Transactional
    public boolean save(Dish dish, List<DishFlavor> flavorList) {
        try {
            //保存菜品
            this.save(dish);
            //保存口味
            flavorList.forEach(dishFlavor -> {
                dishFlavor.setDishId(dish.getId());
            });
            dishFlavorService.saveBatch(flavorList);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    @Transactional
    public boolean update(Dish dish, List<DishFlavor> flavorList) {
        //修改菜品
        this.updateById(dish);
        //删除现有口味
        QueryWrapper<DishFlavor> qw = new QueryWrapper<>();
        qw.lambda().eq(DishFlavor::getDishId, dish.getId());
        dishFlavorService.remove(qw);
        //给传入口味设置菜品id
        flavorList.forEach(flavor -> {
            flavor.setDishId(dish.getId());
        });
        //保存新的口味
        return dishFlavorService.saveBatch(flavorList);
    }

    //根据分类id和菜品name查询菜品的id,name,status,price
    @Override
    public List<Map<String, Object>> findEnableDishListInfo(String categoryId, String name) {
        QueryWrapper<Dish> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().select(Dish::getId, Dish::getName, Dish::getStatus, Dish::getPrice);

        if (StringUtils.isNotEmpty(categoryId)) {
            queryWrapper.lambda().eq(Dish::getCategoryId, categoryId);
        }
        if (StringUtils.isNotEmpty(name)) {
            queryWrapper.lambda().eq(Dish::getName, name);
        }

        queryWrapper.lambda().eq(Dish::getStatus, SystemCode.ENABLED);
        return this.listMaps(queryWrapper);
    }
}
