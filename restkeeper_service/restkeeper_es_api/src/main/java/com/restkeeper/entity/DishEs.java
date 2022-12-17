package com.restkeeper.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

/**
 * 菜品，套餐和elasticsearch中json关系
 */
@Data
public class DishEs implements Serializable {

    private String id;

    private String code;

    private String name;

    private String image;

    private int type;  //1 菜品 2 套餐

    private Integer price;

    //解决json 对象属性不一致问题
    @JSONField(name = "last_update_time")
    private String lastUpdateTime;

    @JSONField(name = "store_id")
    private String storeId;

    @JSONField(name = "shop_id")
    private String shopId;
}
