package com.restkeeper.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.restkeeper.entity.OrderEntity;

public interface IOrderService extends IService<OrderEntity> {
    //下单
    String addOrder(OrderEntity orderEntity);
}
