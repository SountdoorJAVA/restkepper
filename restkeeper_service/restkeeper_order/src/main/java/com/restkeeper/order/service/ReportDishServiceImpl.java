package com.restkeeper.order.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restkeeper.entity.ReportDishEntity;
import com.restkeeper.order.mapper.ReportDishMapper;
import com.restkeeper.service.IReportDishService;
import org.apache.dubbo.config.annotation.Service;

import java.time.LocalDate;
import java.util.List;

@Service(version = "1.0.0", protocol = "dubbo")
@org.springframework.stereotype.Service("reportDishService")
public class ReportDishServiceImpl extends ServiceImpl<ReportDishMapper, ReportDishEntity> implements IReportDishService {

    @Override
    public List<ReportDishEntity> getCategoryAmountCollect(LocalDate start, LocalDate end) {
        QueryWrapper<ReportDishEntity> wrapper = new QueryWrapper<>();
        wrapper.select(
                        "category"
                        , "SUM(dish_number) as dish_number"
                        , "SUM(dish_money) as dish_money")
                .lambda()
                .ge(ReportDishEntity::getLastUpdateTime, start)
                .lt(ReportDishEntity::getLastUpdateTime, end)
                .groupBy(ReportDishEntity::getCategory);

        return this.list(wrapper);
    }

    //查询 菜品名称 和 销售量
    @Override
    public List<ReportDishEntity> getDishRank(LocalDate start, LocalDate end) {
        QueryWrapper<ReportDishEntity> wrapper = new QueryWrapper<>();
        wrapper.select("dish_name", "SUM(dish_number) as dish_number")
                .lambda()
                .ge(ReportDishEntity::getLastUpdateTime, start)
                .lt(ReportDishEntity::getLastUpdateTime, end)
                .groupBy(ReportDishEntity::getDishName);
        return this.list(wrapper);
    }
}
