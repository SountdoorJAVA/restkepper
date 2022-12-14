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

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "Setmeal", description = "套餐")
@TableName(value = "t_setmeal", resultMap = "BaseResultMap")
public class SetMeal extends BaseStoreEntity implements Serializable {

    @TableId(value = "setmeal_id", type = IdType.ASSIGN_ID)
    private String id;

    @TableField(value = "category_id")
    private String categoryId;

    @TableField(value = "setmeal_name")
    private String name;

    @TableField(value = "price")
    private Integer price;

    @TableField(value = "status")
    private Integer status;

    @TableField(value = "code")
    private String code;

    @TableField(value = "description")
    private String description;

    @TableField(value = "image")
    private String image;

    //分类对象
    @TableField(exist = false)
    private DishCategory category;

    //套餐菜品列表
    @TableField(exist = false)
    private List<SetMealDish> dishList;

}
