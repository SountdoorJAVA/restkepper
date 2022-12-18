package com.restkeeper.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.restkeeper.dto.CreditDTO;
import com.restkeeper.dto.DetailDTO;
import com.restkeeper.entity.OrderEntity;

public interface IOrderService extends IService<OrderEntity> {
    //下单
    String addOrder(OrderEntity orderEntity);

    /**
     * 退菜
     *
     * @param detailDTO
     * @return
     */
    public boolean returnDish(DetailDTO detailDTO);


    /**
     * 结账
     *
     * @param orderEntity
     * @return
     */
    boolean pay(OrderEntity orderEntity);

    /**
     * 挂账
     *
     * @param orderEntity
     * @param creditDTO
     * @return
     */
    boolean pay(OrderEntity orderEntity, CreditDTO creditDTO);

    boolean changeTable(String orderId,String targetTableId);

}
