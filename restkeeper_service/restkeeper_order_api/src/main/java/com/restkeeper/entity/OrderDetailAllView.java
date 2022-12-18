package com.restkeeper.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "v_order_detail_all")
public class OrderDetailAllView extends BaseEntity{

    private String dishCategoryName;

    private int dishAmount;

    @TableField(exist = false)
    private int totalCount;

    private int dishType;

    private String dishName;
}
