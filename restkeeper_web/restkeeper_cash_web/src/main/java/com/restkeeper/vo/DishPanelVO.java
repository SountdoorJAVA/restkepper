package com.restkeeper.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 菜品面板展现对象
 */
@Data
public class DishPanelVO {
    @ApiModelProperty(value = "菜品或者套餐id")
    private  String dishId;
    @ApiModelProperty(value = "名称")
    private  String dishName;
    @ApiModelProperty(value = "类型 1 菜品 2 套餐")
    private  Integer type;
    @ApiModelProperty(value = "单价")
    private  Integer price;
    @ApiModelProperty(value = "图片地址")
    private  String image;
    @ApiModelProperty(value = "剩余份数")
    private  Integer remainder;

}
