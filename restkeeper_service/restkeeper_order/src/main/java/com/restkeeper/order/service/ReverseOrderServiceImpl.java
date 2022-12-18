package com.restkeeper.order.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restkeeper.constants.SystemCode;
import com.restkeeper.entity.OrderEntity;
import com.restkeeper.entity.ReverseOrder;
import com.restkeeper.order.mapper.ReverseOrderMapper;
import com.restkeeper.service.IOrderService;
import com.restkeeper.service.IReverseOrderService;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service(version = "1.0.0", protocol = "dubbo")
@org.springframework.stereotype.Service("reverseOrderService")
public class ReverseOrderServiceImpl extends ServiceImpl<ReverseOrderMapper, ReverseOrder> implements IReverseOrderService {
    @Reference(version = "1.0.0", check = false)
    private IOrderService orderService;

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public boolean reverse(ReverseOrder reverseOrder) {
        //修改订单状态为未支付
        String orderId = reverseOrder.getOrderId();
        OrderEntity orderEntity = orderService.getById(orderId);
        orderEntity.setPayStatus(SystemCode.ORDER_STATUS_NOTPAY);
        orderService.updateById(orderEntity);

        //向反结账表插入一条记录
        reverseOrder.setCreateTime(LocalDateTime.now());
        reverseOrder.setOrderNumber(orderEntity.getOrderNumber());
        reverseOrder.setTableId(orderEntity.getTableId());
        reverseOrder.setAmount(orderEntity.getPayAmount());
        String loginUserName = RpcContext.getContext().getAttachment("loginUserName");
        reverseOrder.setOperatorName(loginUserName);
        return this.save(reverseOrder);
    }
}
