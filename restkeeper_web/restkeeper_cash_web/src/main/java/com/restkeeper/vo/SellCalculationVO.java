package com.restkeeper.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 估清VO
 */
@Data
public class SellCalculationVO {
    @ApiModelProperty(value = "菜品或者套餐id")
    private String dishId;

    @ApiModelProperty(value = "类型 1 菜品 2 套餐")
    private Integer dishType;

    @ApiModelProperty(value = "名称")
    private String dishName;

    @ApiModelProperty(value = "估清数目")
    private Integer numbers;

}
