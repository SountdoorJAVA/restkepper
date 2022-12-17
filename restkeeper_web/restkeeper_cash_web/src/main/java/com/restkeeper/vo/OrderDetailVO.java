package com.restkeeper.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 订单菜品列表
 */
@Data
public class OrderDetailVO {

    @ApiModelProperty(value = "菜品或者套餐id")
    private  String dishId;
    @ApiModelProperty(value = "名称")
    private  String dishName;
    @ApiModelProperty(value = "类型 1 菜品 2 套餐")
    private  Integer type;
    @ApiModelProperty(value = "单价")
    private  Integer price;
    @ApiModelProperty(value = "份数")
    private Integer dishNumber;
    @ApiModelProperty(value = "菜品备注")
    private String dishRemark;
    @ApiModelProperty(value = "状态")
    private Integer status=1; //状态 1正常 2 赠菜 3 退菜  4 加菜
    @ApiModelProperty(value = "口味")
    private List<String> flavorList;
}

