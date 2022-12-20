package com.restkeeper.order.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.restkeeper.dto.DayAmountCollectDTO;
import com.restkeeper.dto.PayType;
import com.restkeeper.dto.PayTypeCollectDTO;
import com.restkeeper.dto.PrivilegeDTO;
import com.restkeeper.entity.ReportPay;
import com.restkeeper.order.mapper.ReportPayMapper;
import com.restkeeper.service.IReportPayService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ReportPayServiceImpl extends ServiceImpl<ReportPayMapper, ReportPay> implements IReportPayService {

    @Override
    public List<DayAmountCollectDTO> getDayAmountCollect(LocalDate start, LocalDate end) {
        //对订单汇总表进行查询
        QueryWrapper<ReportPay> wrapper = new QueryWrapper<>();
        wrapper.select("SUM(pay_amount) as pay_amount", "SUM(pay_count) as pay_count", "pay_date")
                .lambda()
                .ge(ReportPay::getLastUpdateTime, start)
                .lt(ReportPay::getLastUpdateTime, end)
                .groupBy(ReportPay::getPayDate)
                .orderByDesc(ReportPay::getPayDate);
        List<ReportPay> reportPayList = this.list(wrapper);

        //转换为前端需要数据类型
        List<DayAmountCollectDTO> fromQuery = reportPayList.stream().map(r -> {
            DayAmountCollectDTO dayAmountCollectDTO = new DayAmountCollectDTO();
            dayAmountCollectDTO.setDate(r.getPayDate());
            dayAmountCollectDTO.setTotalAmount(r.getPayAmount());
            dayAmountCollectDTO.setTotalCount(r.getPayCount());
            return dayAmountCollectDTO;
        }).collect(Collectors.toList());

        List<DayAmountCollectDTO> results = Lists.newArrayList();
        results.addAll(fromQuery);

        //得到天数差
        long daysCount = end.toEpochDay() - start.toEpochDay();

        Stream.iterate(start, i -> i.plusDays(1)).limit(daysCount)
                //得到一个集合,从开始到结束,都是0
                .map(date -> {
                    DayAmountCollectDTO dayAmountCollectDTO = new DayAmountCollectDTO();
                    dayAmountCollectDTO.setDate(date);
                    dayAmountCollectDTO.setTotalCount(0);
                    dayAmountCollectDTO.setTotalAmount(0);
                    return dayAmountCollectDTO;
                })
                .forEach(dto -> {
                    //将数据库查询的结果流式计算,如果是同一天,就将数据量结果覆盖掉结果集里的数据
                    if (!fromQuery.stream().anyMatch(item -> item.getDate().isEqual(dto.getDate()))) {
                        results.add(dto);
                    }
                });

        //results排序
        results.sort((a, b) -> {
            if (a.getDate().isBefore(b.getDate())) {
                return -1;
            }
            if (a.getDate().isBefore(b.getDate())) {
                return 1;
            }
            return 0;
        });

        return results;
    }

    @Override
    public List<PayTypeCollectDTO> getPayTypeCollect(LocalDate start, LocalDate end) {
        QueryWrapper<ReportPay> wrapper = new QueryWrapper<>();
        wrapper.select("sum(pay_amount) as total_amount","pay_type")
                .lambda().ge(ReportPay::getLastUpdateTime,start)
                .lt(ReportPay::getLastUpdateTime,end)
                .isNotNull(ReportPay::getPayType)//支付方式不为null
                .groupBy(ReportPay::getPayType);
        List<ReportPay> reportPayList = this.list(wrapper);

        return reportPayList.stream().map(reportPay -> {

            PayTypeCollectDTO dto = new PayTypeCollectDTO();

            dto.setPayType(reportPay.getPayType());
            dto.setTotalCount(reportPay.getTotalAmount());
            dto.setPayName(PayType.getName(reportPay.getPayType()));

            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public PrivilegeDTO getPrivilegeCollectByDate(LocalDate start, LocalDate end) {
        QueryWrapper<ReportPay> wrapper = new QueryWrapper<>();
        wrapper.select("sum(present_amount) as present_amount","sum(small_amount) as small_amount","sum(free_amount) as free_amount")
                .lambda().ge(ReportPay::getLastUpdateTime,start)
                .lt(ReportPay::getLastUpdateTime,end);
        ReportPay reportPay = this.getOne(wrapper);

        PrivilegeDTO dto = new PrivilegeDTO();

        if (reportPay == null){
            dto.setFreeAmount(0);
            dto.setPresentAmount(0);
            dto.setSmallAmount(0);
        }else{

            dto.setSmallAmount(reportPay.getSmallAmount());
            dto.setPresentAmount(reportPay.getPresentAmount());
            dto.setFreeAmount(reportPay.getFreeAmount());
        }
        return dto;
    }
}
