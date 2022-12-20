package com.restkeeper.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.restkeeper.entity.ReportDishEntity;

import java.time.LocalDate;
import java.util.List;

public interface IReportDishService extends IService<ReportDishEntity> {
    /**
     * 获取一定时期内的菜品分类销售额汇总(用作饼图展示)
     * @param start
     * @param end
     * @return
     */
    List<ReportDishEntity> getCategoryAmountCollect(LocalDate start,LocalDate end);

    /**
     * 获取一定日期内的菜品销量排行
     * @param start
     * @param end
     * @return
     */
    List<ReportDishEntity> getDishRank(LocalDate start,LocalDate end);
}
