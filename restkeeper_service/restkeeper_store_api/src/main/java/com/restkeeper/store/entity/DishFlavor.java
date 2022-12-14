package com.restkeeper.store.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="DishFlavor", description="菜品及套餐分类")
@TableName(value = "t_dish_flavor",resultMap = "BaseResultMap")
public class DishFlavor implements Serializable{
	
    @ApiModelProperty(value = "主键id")
    @TableId(value = "id",type = IdType.ASSIGN_ID)
    private String id;
	
    @ApiModelProperty(value = "菜品id")
    @TableField(value = "dish_id")
    private String dishId;
	
    @ApiModelProperty(value = "口味名")
    @TableField(value = "flavor_name")
    private String flavorName;
	
    @ApiModelProperty(value = "口味标签")
    @TableField(value = "flavor_value")
    private String flavorValue;
	
    @ApiModelProperty(value = "所属商户id")
    @TableField(value = "shop_id")
    protected String shopId;
	
    @ApiModelProperty(value = "门店id")
    protected String storeId;
	
    @ApiModelProperty(value = "最后更新时间")
    @TableField(value = "last_update_time",fill = FieldFill.INSERT_UPDATE)
    protected LocalDateTime lastUpdateTime;
}
