package com.restkeeper.store.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 沽清
 * </p>
 */
@Data
@Accessors(chain = true)
@ApiModel(value="沽清对象", description="沽清")
@TableName(value = "t_sell_calculation")
public class SellCalculation implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    private String id;

    @ApiModelProperty(value = "菜品Id")
    private String dishId;

    @ApiModelProperty(value = "菜品名称")
    private String dishName;

    @ApiModelProperty(value = "1 普通菜品2 套餐")
    private Integer dishType;

    @ApiModelProperty(value = "估清总数")
    private Integer sellLimitTotal;

    @ApiModelProperty(value = "剩余数")
    private Integer remainder;

    @ApiModelProperty(value = "最后更新时间")
    @TableField(value = "last_update_time",fill = FieldFill.INSERT_UPDATE)
    protected LocalDateTime lastUpdateTime;

    @ApiModelProperty(value = "所属商户id")
    @TableField(value = "shop_id")
    protected String shopId;

    @ApiModelProperty(value = "门店id")
    protected String storeId;

}

