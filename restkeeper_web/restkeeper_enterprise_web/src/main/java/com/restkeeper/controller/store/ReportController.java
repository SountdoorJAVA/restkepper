package com.restkeeper.controller.store;

import com.google.common.collect.Lists;
import com.restkeeper.dto.CurrentAmountCollectDTO;

import com.restkeeper.dto.CurrentHourCollectDTO;
import com.restkeeper.dto.DayAmountCollectDTO;
import com.restkeeper.dto.PrivilegeDTO;
import com.restkeeper.service.IOrderDetailService;
import com.restkeeper.service.IOrderService;
import com.restkeeper.service.IReportDishService;
import com.restkeeper.service.IReportPayService;
import com.restkeeper.vo.store.AmountCollectVO;

import com.restkeeper.vo.store.BarChartCollectVO;
import com.restkeeper.vo.store.PieVo;
import com.restkeeper.vo.store.PrivilegeVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = {"报表"})
@RestController
@RequestMapping("/report")
public class ReportController {

    @Reference(version = "1.0.0", check = false)
    private IOrderService orderService;

    @ApiOperation(value = "获取当日销量数据")
    @GetMapping("/amountCollect")
    public AmountCollectVO getAmountCollect() {
        AmountCollectVO vo = new AmountCollectVO();
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(1);
        CurrentAmountCollectDTO dto = orderService.getCurrentCollect(start, end);
        BeanUtils.copyProperties(dto, vo);

        return vo;
    }

    @ApiOperation(value = "获取当天24小时销量数据汇总")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "type", value = "类型(1:金额;2:数量)", required = true, dataType = "Int")})
    @GetMapping("/hourCollect/{type}")
    public BarChartCollectVO getHourCollect(@PathVariable Integer type) {
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(1);

        List<CurrentHourCollectDTO> dtos = orderService.getCurrentHourCollect(start, end, type);
        BarChartCollectVO vo = new BarChartCollectVO();
        dtos.forEach(d -> {
            vo.getXAxis().add(d.getCurrentDateHour() + "");
            vo.getSeries().add(d.getTotalAmount());
        });

        return vo;
    }

    @Reference(version = "1.0.0", check = false)
    private IOrderDetailService orderDetailService;

    @ApiOperation(value = "获取菜品分类销售排行")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "type", value = "类型(1:金额;2:数量)", required = true, dataType = "Int")})
    @GetMapping("/categoryCollect/{type}")
    public List<PieVo> getCategoryAmountCollect(@PathVariable int type) {
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(1);
        if (type == 1) {
            return orderDetailService.getCurrentCategoryAmountCollect(start, end)
                    .stream()
                    .map(d -> {
                        PieVo pieVo = new PieVo();
                        pieVo.setValue(d.getDishAmount());
                        pieVo.setName(d.getDishCategoryName());
                        return pieVo;
                    })
                    .collect(Collectors.toList());
        } else if (type == 2) {
            return orderDetailService.getCurrentCategoryCountCollect(start, end)
                    .stream()
                    .map(d -> {
                        PieVo pieVo = new PieVo();
                        pieVo.setValue(d.getTotalCount());
                        pieVo.setName(d.getDishCategoryName());
                        return pieVo;
                    })
                    .collect(Collectors.toList());
        }
        return null;
    }

    @ApiOperation(value = "获取当日菜品销售排行")
    @GetMapping("/currentDishRank")
    public BarChartCollectVO getCurrentDishRank() {
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(1);
        BarChartCollectVO result = new BarChartCollectVO();
        orderDetailService.getCurrentDishRank(start, end)
                .forEach(d -> {
                    result.getXAxis().add(d.getDishName());
                    result.getSeries().add(d.getTotalCount());
                });
        return result;
    }

    @ApiOperation(value = "获取各种支付类型数据汇总")
    @GetMapping("/payTypeCollect")
    public List<PieVo> getPayTypeCollect() {
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(1);
        return orderService.getPayTypeCollect(start, end)
                .stream()
                .map(d -> {
                    //通过map将DTO转换成PieVo
                    PieVo pieVo = new PieVo();
                    pieVo.setName(d.getPayName());
                    pieVo.setValue(d.getTotalCount());
                    return pieVo;
                })
                .collect(Collectors.toList());
    }

    @ApiOperation(value = "获取各种优惠类型数据汇总")
    @GetMapping("/privilegeCollect")
    public PrivilegeVO getPrivilegeCollect() {
        PrivilegeVO privilegeVO = new PrivilegeVO();
        List<PieVo> pieVOList = Lists.newArrayList();
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(1);
        PrivilegeDTO privilegeDTO = orderService.getPrivilegeCollect(start, end);
        //优惠总金额
        double total = privilegeDTO.getPresentAmount() + privilegeDTO.getFreeAmount() + privilegeDTO.getSmallAmount();

        //赠菜
        PieVo present = new PieVo();
        present.setName("赠菜");
        present.setValue(privilegeDTO.getPresentAmount());
        present.setPercent(((double) privilegeDTO.getPresentAmount()) / total * 100);
        pieVOList.add(present);

        //免单
        PieVo free = new PieVo();
        free.setName("免单");
        free.setValue(privilegeDTO.getFreeAmount());
        free.setPercent(((double) privilegeDTO.getFreeAmount()) / total * 100);
        pieVOList.add(free);

        //抹零
        PieVo small = new PieVo();
        small.setName("抹零");
        small.setValue(privilegeDTO.getSmallAmount());
        small.setPercent(((double) privilegeDTO.getSmallAmount()) / total * 100);
        pieVOList.add(small);

        privilegeVO.setDataList(pieVOList);
        privilegeVO.setTotal(privilegeDTO.getPresentAmount() + privilegeDTO.getFreeAmount() + privilegeDTO.getSmallAmount());

        return privilegeVO;
    }


    @Reference(version = "1.0.0", check = false)
    private IReportPayService reportPayService;

    @ApiOperation(value = "获取一定日期之内的销售趋势")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "type", value = "1:按金额；2:按单数", required = true, dataType = "Int"),
            @ApiImplicitParam(paramType = "path", name = "start", value = "开始日期", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "path", name = "end", value = "结束日期", required = true, dataType = "String"),
    })
    @GetMapping("/dayAmountCollect/{type}/{start}/{end}")
    public BarChartCollectVO getDayAmountCollect(@PathVariable int type, @PathVariable String start, @PathVariable String end) {
        LocalDate startDate = LocalDate.parse(start, DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate endDate = LocalDate.parse(end, DateTimeFormatter.ISO_LOCAL_DATE);
        BarChartCollectVO vo = new BarChartCollectVO();
        List<DayAmountCollectDTO> results = reportPayService.getDayAmountCollect(startDate, endDate);
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern("MM月dd日");

        if (type == 1) {
            vo.setXAxis(results.stream().map(r -> r.getDate().format(formatters)).collect(Collectors.toList()));
            vo.setSeries(results.stream().map(r -> r.getTotalAmount()).collect(Collectors.toList()));
        }
        if (type == 2) {
            vo.setXAxis(results.stream().map(r -> r.getDate().getDayOfMonth() + "").collect(Collectors.toList()));
            vo.setSeries(results.stream().map(r -> r.getTotalCount()).collect(Collectors.toList()));
        }

        return vo;
    }

    @ApiOperation(value = "获取时间范围之内的各种支付类型数据汇总")
    @GetMapping("/datePayTypeCollect/{start}/{end}")
    public List<PieVo> getDatePayTypeCollect(@PathVariable String start, @PathVariable String end) {
        LocalDate startDate = LocalDate.parse(start, DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate endDate = LocalDate.parse(end, DateTimeFormatter.ISO_LOCAL_DATE);
        return reportPayService
                .getPayTypeCollect(startDate, endDate)
                .stream()
                .map(d -> {
                    PieVo pieVo = new PieVo();
                    pieVo.setName(d.getPayName());
                    pieVo.setValue(d.getTotalCount());
                    return pieVo;
                }).collect(Collectors.toList());
    }

    @Reference(version = "1.0.0", check = false)
    private IReportDishService reportDishService;

    @ApiOperation(value = "获取时间范围之内的菜品类别销售汇总")
    @GetMapping("/dateCategoryCollect/{type}/{start}/{end}")
    public List<PieVo> getDateCategoryCollect(@PathVariable int type, @PathVariable String start, @PathVariable String end) {
        LocalDate startDate = LocalDate.parse(start, DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate endDate = LocalDate.parse(end, DateTimeFormatter.ISO_LOCAL_DATE);

        if (type == 1) { //按金额
            return reportDishService
                    .getCategoryAmountCollect(startDate, endDate)
                    .stream()
                    .map(d -> {
                        PieVo pieVo = new PieVo();
                        pieVo.setValue(d.getDishMoney());
                        pieVo.setName(d.getCategory());
                        return pieVo;
                    }).collect(Collectors.toList());
        } else if (type == 2) { //按销售量
            return reportDishService
                    .getCategoryAmountCollect(startDate, endDate)
                    .stream()
                    .map(d -> {
                        PieVo pieVo = new PieVo();
                        pieVo.setValue(d.getDishNumber());
                        pieVo.setName(d.getCategory());
                        return pieVo;
                    }).collect(Collectors.toList());
        }

        return null;
    }

    @ApiOperation(value = "获取时间范围之内的菜品销售排行")
    @GetMapping("/dishRankForDate/{start}/{end}")
    public BarChartCollectVO getDishRank(@PathVariable String start, @PathVariable String end) {
        LocalDate startDate = LocalDate.parse(start, DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate endDate = LocalDate.parse(end, DateTimeFormatter.ISO_LOCAL_DATE);
        BarChartCollectVO result = new BarChartCollectVO();
        reportDishService
                .getDishRank(startDate, endDate)
                .forEach(d -> {
                    result.getXAxis().add(d.getDishName());
                    result.getSeries().add(d.getDishNumber());
                });
        return result;
    }
}
