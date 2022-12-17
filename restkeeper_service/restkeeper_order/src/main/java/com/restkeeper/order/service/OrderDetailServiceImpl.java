package com.restkeeper.order.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restkeeper.entity.OrderDetailEntity;
import com.restkeeper.order.mapper.OrderDetailMapper;
import com.restkeeper.service.IOrderDetailService;
import org.apache.dubbo.config.annotation.Service;

@org.springframework.stereotype.Service("orderDetailService")
@Service(version = "1.0.0",protocol = "dubbo")
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetailEntity> implements IOrderDetailService {
}
