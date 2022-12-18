package com.restkeeper.order.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restkeeper.entity.OrderDetailMealEntity;
import com.restkeeper.order.mapper.OrderDetailMealMapper;
import com.restkeeper.service.IOrderDetailMealService;
import org.apache.dubbo.config.annotation.Service;

@Service(version = "1.0.0",protocol = "dubbo")
@org.springframework.stereotype.Service("orderDetailMealService")
public class OrderDetailMealServiceImpl extends ServiceImpl<OrderDetailMealMapper, OrderDetailMealEntity> implements IOrderDetailMealService {
}
