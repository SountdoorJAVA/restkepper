package com.restkeeper.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restkeeper.entity.OrderEntity;
import com.restkeeper.entity.ReportPay;

import com.restkeeper.mapper.OrderMapper;
import com.restkeeper.mapper.ReportPayMapper;
import com.restkeeper.service.ReportPayService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Service(version = "1.0.0", protocol = "dubbo")
@org.springframework.stereotype.Service("reportPayService")
public class ReportPayServiceImpl extends ServiceImpl<ReportPayMapper, ReportPay> implements ReportPayService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ReportPayMapper reportPayMapper;

    @Override
    public void generateData() {
        //统计前一天的数据
        LocalDate end = LocalDate.now();
        LocalDate start = end.plusDays(-1);

        QueryWrapper<OrderEntity> wrapper = new QueryWrapper<>();
        wrapper.select("sum(total_amount) as total_amount", //应收金额
                        "sum(free_amount) as free_amount",  //免单金额
                        "sum(present_amount) as present_amount", //赠菜金额
                        "sum(small_amount) as small_amount", //抹零金额
                        "sum(pay_amount) as pay_amount", //实收金额
                        "sum(person_numbers) as person_numbers", //就餐人数
                        "count(pay_type) as pay_count",//支付笔数
                        "pay_type",
                        "shop_id",
                        "store_id").lambda()
                .ge(OrderEntity::getLastUpdateTime, start)
                .lt(OrderEntity::getLastUpdateTime, end)
                .groupBy(OrderEntity::getPayType, OrderEntity::getStoreId, OrderEntity::getShopId);
        List<OrderEntity> orderEntityList = orderMapper.selectList(wrapper);

        orderEntityList.forEach(orderEntity -> {
            ReportPay reportPay = new ReportPay();

            reportPay.setFreeAmount(orderEntity.getFreeAmount());
            reportPay.setPayAmount(orderEntity.getPayAmount());
            reportPay.setSmallAmount(orderEntity.getSmallAmount());
            reportPay.setPresentAmount(orderEntity.getPresentAmount());
            reportPay.setPayDate(end);
            reportPay.setPayType(orderEntity.getPayType());
            reportPay.setPersonNumbers(orderEntity.getPersonNumbers());
            reportPay.setShopId(orderEntity.getShopId());
            reportPay.setStoreId(orderEntity.getStoreId());
            reportPay.setTotalAmount(orderEntity.getTotalAmount());
            reportPay.setPayCount(orderEntity.getPayCount());
            reportPay.setIsDeleted(0);
            reportPay.setLastUpdateTime(LocalDateTime.now());

            reportPayMapper.insert(reportPay);
        });
    }
}
