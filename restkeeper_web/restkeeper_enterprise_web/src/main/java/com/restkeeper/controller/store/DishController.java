package com.restkeeper.controller.store;

import com.restkeeper.exception.BussinessException;
import com.restkeeper.store.entity.Dish;
import com.restkeeper.store.entity.DishFlavor;
import com.restkeeper.store.service.IDishService;
import com.restkeeper.vo.store.DishFlavorVO;
import com.restkeeper.vo.store.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author MORRIS --> Java
 * @date 2022-12-14 19:53:36
 */
@Slf4j
@RestController
@Api(tags = {"菜品管理"})
@RequestMapping("/dish")
public class DishController {
    @Reference(version = "1.0.0", check = false)
    private IDishService dishService;

    @ApiOperation(value = "添加菜品")
    @PostMapping("/add")
    public Boolean add(@RequestBody DishVO dishVO) {
        //提取菜品
        Dish dish = new Dish();
        BeanUtils.copyProperties(dish, dishVO);
        //提取口味
        List<DishFlavor> flavorList = new ArrayList<>();
        dishVO.getDishFlavors().forEach(dishFlavorVO -> {
            DishFlavor dishFlavor = new DishFlavor();
            BeanUtils.copyProperties(dishFlavor, dishFlavorVO);
            flavorList.add(dishFlavor);
        });
        return dishService.save(dish, flavorList);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "根据id获取菜品信息")
    public DishVO getDish(@PathVariable String id) {
        Dish dish = dishService.getById(id);
        if (dish == null) {
            throw new BussinessException("菜品不存在");
        }
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dishVO, dish);

        List<DishFlavorVO> dishFlavorList = new ArrayList<>();
        dish.getFlavorList().forEach(flavor -> {
            DishFlavorVO dishFlavorVO = new DishFlavorVO();
            //设置口味名称
            dishFlavorVO.setFlavor(flavor.getFlavorName());
            //设置口味标签集合
            String flavorValue = flavor.getFlavorValue();
            String subFlavorValue = flavorValue.substring(flavorValue.indexOf("[") + 1, flavorValue.indexOf("]"));
            if (StringUtils.isNotEmpty(subFlavorValue)) {
                val flavorArray = Arrays.asList(subFlavorValue.split(","));
                dishFlavorVO.setFlavorData(flavorArray);
            }
            dishFlavorList.add(dishFlavorVO);
        });

        dishVO.setDishFlavors(dishFlavorList);
        return dishVO;
    }

    @ApiOperation(value = "修改菜品")
    @PutMapping("/update")
    public boolean update(@RequestBody DishVO dishVO) {
        val dish = new Dish();
        BeanUtils.copyProperties(dish, dishVO);

        List<DishFlavor> flavorList = new ArrayList<>();
        val dishFlavors = dishVO.getDishFlavors();
        for (DishFlavorVO dishFlavorVO : dishFlavors) {
            val dishFlavor = new DishFlavor();
            BeanUtils.copyProperties(dishFlavor, dishFlavorVO);
            flavorList.add(dishFlavor);
        }

        return dishService.update(dish, flavorList);
    }

    @ApiOperation(value = "查询可用的菜品列表")
    @GetMapping("/findEnableDishList/{categoryId}")
    public List<Map<String, Object>> findEnableDishList(
            @PathVariable String categoryId, 
            @RequestParam(value = "name", required = false) String name) {
        return dishService.findEnableDishListInfo(categoryId, name);
    }
}
