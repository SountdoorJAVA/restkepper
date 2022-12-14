package com.restkeeper.controller.store;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.common.collect.Lists;
import com.restkeeper.constants.SystemCode;
import com.restkeeper.exception.BussinessException;
import com.restkeeper.response.vo.PageVO;
import com.restkeeper.store.entity.SetMeal;
import com.restkeeper.store.entity.SetMealDish;
import com.restkeeper.store.service.ISetMealService;
import com.restkeeper.vo.store.SetMealDishVO;
import com.restkeeper.vo.store.SetMealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author MORRIS --> Java
 * @date 2022-12-15 00:05:20
 */
@Slf4j
@Api(tags = {"套餐管理"})
@RestController
@RequestMapping("/setMeal")
public class SetMealController {

    @Reference(version = "1.0.0", check = false)
    private ISetMealService setMealService;

    @ApiOperation("套餐分页查询")
    @GetMapping("/queryPage/{page}/{pageSize}")
    public PageVO<SetMeal> queryPage(@PathVariable("page") Integer page,
                                     @PathVariable("pageSize") Integer pageSize,
                                     @RequestParam(value = "name", required = false) String name) {
        return new PageVO<>(setMealService.queryPage(page, pageSize, name));
    }

    @ApiOperation(value = "添加套餐")
    @PostMapping("/add")
    public boolean add(@RequestBody SetMealVO setMealVO) {
        val setMeal = new SetMeal();
        BeanUtils.copyProperties(setMeal, setMealVO);

        val setMealDishList = new ArrayList<SetMealDish>();

        if (setMealVO.getDishList() != null) {
            setMealVO.getDishList().forEach(dish -> {
                SetMealDish setMealDish = new SetMealDish();
                setMealDish.setDishId(dish.getDishId());//菜品id
                setMealDish.setDishName(dish.getDishName());//菜品名称
                setMealDish.setIndex(0);//下单量
                setMealDish.setDishCopies(dish.getCopies());//菜品分数
                setMealDishList.add(setMealDish);
            });
        }
        return setMealService.add(setMeal, setMealDishList);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "根据id获取套餐信息")
    public SetMealVO getDish(@PathVariable String id) {

        SetMeal setMeal = setMealService.getById(id);
        if (setMeal == null) {
            throw new BussinessException("套餐不存在");
        }
        //套餐信息
        SetMealVO setMealVo = new SetMealVO();
        BeanUtils.copyProperties(setMeal, setMealVo);
        //菜品信息
        List<SetMealDish> setMealDishList = setMeal.getDishList();
        List<SetMealDishVO> setMealDishVOList = new ArrayList<>();
        for (SetMealDish setMealDish : setMealDishList) {
            SetMealDishVO setMealDishVO = new SetMealDishVO();

            setMealDishVO.setDishId(setMealDish.getDishId());
            setMealDishVO.setDishName(setMealDish.getDishName());
            setMealDishVO.setCopies(setMealDish.getDishCopies());

            setMealDishVOList.add(setMealDishVO);
        }
        setMealVo.setDishList(setMealDishVOList);

        return setMealVo;
    }

    @PutMapping("/update")
    @ApiOperation(value = "更新套餐")
    public boolean update(@RequestBody SetMealVO setMealVo) {
        //获取套餐信息
        SetMeal setMeal = setMealService.getById(setMealVo.getId());
        //重新赋值
        BeanUtils.copyProperties(setMealVo, setMeal);
        //清空菜品
        setMeal.setDishList(null);

        //得到需要保存关联的菜品列表
        List<SetMealDish> setMealDishList = Lists.newArrayList();
        if (setMealVo.getDishList() != null) {
            setMealVo.getDishList().forEach(d -> {
                SetMealDish setMealDish = new SetMealDish();
                setMealDish.setIndex(0);
                setMealDish.setDishCopies(d.getCopies());
                setMealDish.setDishId(d.getDishId());
                setMealDishList.add(setMealDish);
            });
        }

        return setMealService.update(setMeal, setMealDishList);
    }

    @PutMapping("/stop/{id}")
    @ApiOperation(value = "停用套餐")
    public Boolean stop(@PathVariable String id) {
        return setMealService.pauseSetMeal(id);
    }
}
