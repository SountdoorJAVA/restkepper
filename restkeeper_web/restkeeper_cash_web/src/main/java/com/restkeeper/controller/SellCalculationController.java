package com.restkeeper.controller;

import com.restkeeper.store.entity.SellCalculation;
import com.restkeeper.store.service.ISellCalculationService;
import com.restkeeper.vo.SellCalculationVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author MORRIS --> Java
 * @date 2022-12-17 01:46:48
 */
@RestController
@RequestMapping("/sellCalculation")
@Api(tags = {"估清接口"})
public class SellCalculationController {
    @Reference(version = "1.0.0", check = false)
    private ISellCalculationService sellCalculationService;

    @ApiOperation(value = "添加估清")
    @PostMapping("/add")
    public boolean add(@RequestBody SellCalculationVO sellCalculationVO) {

        SellCalculation sellCalculation = new SellCalculation();
        sellCalculation.setDishId(sellCalculationVO.getDishId());
        sellCalculation.setDishName(sellCalculationVO.getDishName());
        sellCalculation.setDishType(sellCalculationVO.getDishType());
        sellCalculation.setSellLimitTotal(sellCalculationVO.getNumbers());
        sellCalculation.setRemainder(sellCalculationVO.getNumbers());
        return sellCalculationService.save(sellCalculation);
    }

    @ApiOperation(value = "删除沽清")
    @DeleteMapping("/delete")
    public boolean delete(@RequestBody List<String> ids){
        return sellCalculationService.removeByIds(ids);
    }
}
