package com.restkeeper.store.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restkeeper.exception.BussinessException;
import com.restkeeper.store.entity.SetMeal;
import com.restkeeper.store.entity.SetMealDish;
import com.restkeeper.store.mapper.SetMealMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * @author MORRIS --> Java
 * @date 2022-12-14 23:52:47
 */
@Service(version = "1.0.0", protocol = "dubbo")
@org.springframework.stereotype.Service("serMealService")
public class SetMealServiceImpl extends ServiceImpl<SetMealMapper, SetMeal> implements ISetMealService {
    @Override
    public IPage<SetMeal> queryPage(int pageNum, int pageSize, String name) {
        IPage<SetMeal> page = new Page<>(pageNum, pageSize);
        QueryWrapper<SetMeal> qw = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(name)) {
            qw.lambda().eq(SetMeal::getName, name);
        }
        return this.page(page, qw);
    }

    @Autowired
    private ISetMealDishService setMealDishService;

    @Override
    @Transactional
    public boolean add(SetMeal setmeal, List<SetMealDish> setMealDishes) {
        try {
            //保存套餐信息
            this.save(setmeal);
            //保存套餐菜品信息
            for (SetMealDish setMealDish : setMealDishes) {
                setMealDish.setSetMealId(setmeal.getId());
            }
            setMealDishService.saveBatch(setMealDishes);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    @Override
    @Transactional
    public boolean update(SetMeal setMeal, List<SetMealDish> setMealDishes) {
        try {
            //修改套餐基础信息
            this.updateById(setMeal);
            //删除原有的菜品关联关系
            if (setMealDishes != null || setMealDishes.size()>0){
                QueryWrapper<SetMealDish> queryWrapper =new QueryWrapper<>();
                queryWrapper.lambda().eq(SetMealDish::getSetMealId,setMeal.getId());
                setMealDishService.remove(queryWrapper);
                //重建菜品的关联关系
                setMealDishes.forEach((setMealDish)->{
                    setMealDish.setSetMealId(setMeal.getId());
                });
                setMealDishService.saveBatch(setMealDishes);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Boolean pauseSetMeal(String id) {
        UpdateWrapper<SetMeal> uw = new UpdateWrapper<>();
        uw.lambda().set(SetMeal::getStatus, 0).eq(SetMeal::getId, id);
        return this.update(uw);
    }
}
