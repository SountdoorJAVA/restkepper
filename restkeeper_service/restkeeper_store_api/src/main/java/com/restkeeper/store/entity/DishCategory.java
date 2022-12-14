package com.restkeeper.store.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 菜品及套餐分类
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="DishCategory", description="菜品及套餐分类")
@TableName(value = "t_category",resultMap = "BaseResultMap")
public class DishCategory extends BaseStoreEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "分类id")
    @TableId(type = IdType.ASSIGN_ID)
    private String categoryId;

    @ApiModelProperty(value = "类型1 菜品 2 套餐")
    @TableField(value = "type")
    private Integer type;

    @ApiModelProperty(value = "商品码")
    @TableField(value = "`name`")
    private String name;

    @ApiModelProperty(value = "顺序")
    @TableField(value = "t_order")
    private Integer torder;

    @ApiModelProperty(value = "菜品列表")
    @TableField(exist = false)
    private List<Dish> dishList;
}
