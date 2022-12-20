package com.restkeeper.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.restkeeper.entity.OrderDetailAllView;
import com.restkeeper.entity.OrderDetailEntity;

import java.time.LocalDate;
import java.util.List;

public interface IOrderDetailService extends IService<OrderDetailEntity> {
    /**
     * 按销售额汇总当天菜品分类数据
     * @return
     */
    List<OrderDetailAllView> getCurrentCategoryAmountCollect(LocalDate start, LocalDate end);

    /**
     * 按销量汇总当天菜品分类数据
     * @return
     */
    List<OrderDetailAllView> getCurrentCategoryCountCollect(LocalDate start,LocalDate end);

    //获取当日菜品销售排行
    List<OrderDetailAllView> getCurrentDishRank(LocalDate start,LocalDate end);
}
