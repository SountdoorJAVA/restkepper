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
 * 菜品
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value="Dish", description="菜品")
@TableName(value = "t_dish", resultMap = "BaseResultMap")
public class Dish extends BaseStoreEntity implements Serializable{
    @ApiModelProperty(value = "菜品id")
    @TableId(value = "dish_id",type = IdType.ASSIGN_ID)
    private String id;
    @ApiModelProperty(value = "菜品名称")
    @TableField(value = "dish_name")
    private String name;
    @ApiModelProperty(value = "分类id")
    @TableField(value = "category_id")
    private String categoryId;
    @ApiModelProperty(value = "价格")
    @TableField(value = "price")
    private Integer price;
    @ApiModelProperty(value = "菜品code")
    @TableField(value = "code")
    private String code;
    @ApiModelProperty(value = "图片路径")
    @TableField(value = "image")
    private String image;
    @TableField(value = "description")
    @ApiModelProperty(value = "备注")
    private String description;
    @ApiModelProperty(value = "状态")
    @TableField(value = "status")
    private Integer status;

    @ApiModelProperty(value = "所属分类对象")
    @TableField(exist = false)
    private DishCategory dishCategory;
    @ApiModelProperty(value = "口味列表")
    @TableField(exist = false)
    private List<DishFlavor> flavorList;
}
