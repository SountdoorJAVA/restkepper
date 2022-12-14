package com.restkeeper.vo.store;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SetMealDishVO {


    /**
     * 菜品Id
     */
    @ApiModelProperty(value = "菜品id")
    private String dishId;

    /**
     * 菜品名称
     */
    @ApiModelProperty(value = "菜品名称")
    private String dishName;
    /**
     * 份数
     */
    @ApiModelProperty(value = "份数")
    private int copies;


}
