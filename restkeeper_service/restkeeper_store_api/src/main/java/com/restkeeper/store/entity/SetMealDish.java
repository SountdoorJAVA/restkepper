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
@ApiModel(value="DishCategory", description="套餐与口味关联")
@TableName(value = "t_setmeal_dish",resultMap = "BaseResultMap")
public class SetMealDish implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id",type = IdType.ASSIGN_ID)
    private String id;
	
    @TableField(value = "setmeal_id")
    private String setMealId;
	
    @TableField(value = "dish_id")
    private String dishId;
	
    @TableField(value = "dish_name")
    private String dishName;
	
    @TableField(value = "dish_copies")
    private Integer dishCopies;
	
    @TableField(value = "t_order")
    private Integer index;

    @ApiModelProperty(value = "所属商户id")
    @TableField(value = "shop_id")
    protected String shopId;

    @ApiModelProperty(value = "门店id")
    @TableField(value = "store_id")
    protected String storeId;
	
	@ApiModelProperty(value = "最后更新时间")
    @TableField(value = "last_update_time",fill = FieldFill.INSERT_UPDATE)
    protected LocalDateTime lastUpdateTime;
}
