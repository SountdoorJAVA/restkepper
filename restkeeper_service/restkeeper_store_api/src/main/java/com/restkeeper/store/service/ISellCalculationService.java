package com.restkeeper.store.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.restkeeper.store.entity.SellCalculation;

public interface ISellCalculationService extends IService<SellCalculation> {
    Integer getRemainderCount(String dishId);

    //扣减
    void decrease(String dishId, Integer dishNumber);
}
