package com.restkeeper.vo.store;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class SetMealVO {

    @ApiModelProperty(value = "套餐id")
    private String id; //添加时候不传

    @ApiModelProperty(value = "分类id")
    private String categoryId;

    @ApiModelProperty(value = "套餐名称")
    private String name;

    @ApiModelProperty(value = "套餐价格")
    private Integer price;

    @ApiModelProperty(value = "状态 0停售，1起售")
    private Integer status;

    @ApiModelProperty(value = "商品码")
    private String code;

    @ApiModelProperty(value = "描述信息")
    private String description;

    @ApiModelProperty(value = "图片")
    private String image;

    @ApiModelProperty(value = "菜品")
    private List<SetMealDishVO> dishList;

}
