package com.restkeeper.store.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restkeeper.store.entity.SetMealDish;
import com.restkeeper.store.mapper.SetMealDishMapper;
import org.apache.dubbo.config.annotation.Service;

/**
 * @author MORRIS --> Java
 * @date 2022-12-15 00:39:53
 */
@Service(version = "1.0.0",protocol = "dubbo")
@org.springframework.stereotype.Service("setMealDishService")
public class SetMealDishServiceImpl extends ServiceImpl<SetMealDishMapper, SetMealDish> implements ISetMealDishService {
}
