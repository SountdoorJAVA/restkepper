package com.restkeeper.store.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restkeeper.store.entity.DishFlavor;
import com.restkeeper.store.mapper.DishFlavorMapper;
import org.apache.dubbo.config.annotation.Service;

/**
 * @author MORRIS --> Java
 * @date 2022-12-14 19:36:53
 */
@Service(version = "1.0.0", protocol = "dubbo")
@org.springframework.stereotype.Service("dishFlavorService")
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements IDishFlavorService {
}
