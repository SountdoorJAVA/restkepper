package com.restkeeper.store.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restkeeper.store.entity.SellCalculation;
import com.restkeeper.store.mapper.SellCalculationMapper;
import org.apache.dubbo.config.annotation.Service;

@org.springframework.stereotype.Service("sellCalculationService")
@Service(version = "1.0.0", protocol = "dubbo")
public class SellCalculationServiceImpl extends ServiceImpl<SellCalculationMapper, SellCalculation> implements ISellCalculationService {
    @Override
    public Integer getRemainderCount(String dishId) {
        QueryWrapper<SellCalculation> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().select(SellCalculation::getRemainder).eq(SellCalculation::getDishId, dishId);
        SellCalculation sellCalculation = this.getOne(queryWrapper);
        if (sellCalculation == null) {
            //估清表中没数据，代表该菜品为无限制菜品,返回-1
            return -1;
        }
        return sellCalculation.getRemainder();
    }
}
