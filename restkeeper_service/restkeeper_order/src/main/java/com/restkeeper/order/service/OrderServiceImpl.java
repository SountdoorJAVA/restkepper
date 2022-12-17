package com.restkeeper.order.service;

import com.alibaba.nacos.client.utils.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restkeeper.entity.OrderDetailEntity;
import com.restkeeper.entity.OrderEntity;
import com.restkeeper.exception.BussinessException;
import com.restkeeper.order.mapper.OrderMapper;
import com.restkeeper.service.IOrderDetailService;
import com.restkeeper.service.IOrderService;
import com.restkeeper.store.service.ISellCalculationService;
import com.restkeeper.tenant.TenantContext;
import com.restkeeper.utils.SequenceUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@org.springframework.stereotype.Service("orderService")
@Service(version = "1.0.0", protocol = "dubbo")
public class OrderServiceImpl extends ServiceImpl<OrderMapper, OrderEntity> implements IOrderService {
    @Autowired
    @Qualifier("orderDetailService")
    private IOrderDetailService orderDetailService;
    @Reference(version = "1.0.0", check = false)
    private ISellCalculationService sellCalculationService;

    @Override
    @Transactional
    public String addOrder(OrderEntity orderEntity) {
        //判断订单有无流水号
        if (StringUtils.isEmpty(orderEntity.getOrderNumber())) {
            //生成订单流水号
            String storeId = RpcContext.getContext().getAttachment("storeId");
            orderEntity.setOrderNumber(SequenceUtils.getSequence(storeId));
        }
        //保存到订单表,RpcContext内数据被使用
        this.saveOrUpdate(orderEntity);

        //获取订单详情
        List<OrderDetailEntity> orderDetailEntities = orderEntity.getOrderDetails();
        orderDetailEntities.forEach(orderDetailEntity -> {
            //设置订单详情类和订单的关联
            orderDetailEntity.setOrderId(orderEntity.getOrderId());
            orderDetailEntity.setOrderNumber(SequenceUtils.getSequenceWithPrefix(orderEntity.getOrderNumber()));

            //估清检查
            //得到当前菜品估量
            TenantContext.addAttachment("storeId", RpcContext.getContext().getAttachment("storeId"));
            TenantContext.addAttachment("shopId", RpcContext.getContext().getAttachment("shopId"));
            Integer count = sellCalculationService.getRemainderCount(orderDetailEntity.getDishId());
            //如果是-1,该菜品不限次数
            if (count != -1) {
                //如果当前菜品下单量超过估清量
                if (count < orderDetailEntity.getDishNumber()) {
                    throw new BussinessException(orderDetailEntity.getDishName() + "超过估清数目");
                }
                //不超过估清量,就完成扣减估清量
                sellCalculationService.decrease(orderDetailEntity.getDishId(), orderDetailEntity.getDishNumber());
            }

        });
        //保存到订单详情表
        orderDetailService.saveBatch(orderDetailEntities);

        return orderEntity.getOrderId();
    }
}
