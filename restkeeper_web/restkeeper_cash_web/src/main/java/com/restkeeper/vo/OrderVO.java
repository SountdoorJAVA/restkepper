package com.restkeeper.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 下单订单vo
 */
@Data
public class OrderVO {
    @ApiModelProperty(value = "桌台号")
    private String tableId;
    @ApiModelProperty(value = "就餐人数")
    private int personNumbers;
    @ApiModelProperty(value = "总价")
    private Integer totalAmount;
    @ApiModelProperty(value = "整单备注")
    private String orderRemark;
    @ApiModelProperty(value = "订单菜品列表")
    private List<OrderDetailVO> dishes;

}

