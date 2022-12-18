package com.restkeeper.constants;

import lombok.Getter;

/**
 * 菜品订单类型
 */
@Getter
public enum OrderDetailType {

    NORMAL_DISH(1,"正常下单"),
    PRESENT_DISH(2,"赠菜"),
    RETURN_DISH(3,"退菜"),
    PLUS_DISH(4,"加菜");

    private Integer type;
    private String desc;

    OrderDetailType(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }
}
