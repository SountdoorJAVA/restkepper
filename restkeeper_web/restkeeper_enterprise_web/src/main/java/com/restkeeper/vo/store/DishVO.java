package com.restkeeper.vo.store;

import com.restkeeper.constants.SystemCode;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class DishVO {

    @ApiModelProperty(value = "菜品id")
    private String id;  //添加时候可以不传
	
    @ApiModelProperty(value = "菜品名称")
    private String name;
	
    @ApiModelProperty(value = "分类id")
    private String categoryId;
	
    @ApiModelProperty(value = "价格")
    private Integer price;
	
    @ApiModelProperty(value = "菜品code")
    private String code;
	
    @ApiModelProperty(value = "图片路径")
    private String image;
	
    @ApiModelProperty(value = "备注")
    private String description;
	
    @ApiModelProperty(value = "状态 0 停售，1起售")
    private Integer status = SystemCode.ENABLED; //默认起售
	
    @ApiModelProperty(value = "状态")
    private List<DishFlavorVO> dishFlavors;

}
