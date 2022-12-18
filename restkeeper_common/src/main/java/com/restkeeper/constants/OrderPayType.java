package com.restkeeper.constants;

import lombok.Getter;

/**
 * 订单支付方式
 */
@Getter
public enum OrderPayType {
    FREE(0,"免单"),
    PRESENT_DISH(1,"现金"),
    WEIXIN(2,"微信"),
    ALIPAY(3,"支付宝"),
    BANKCARD(4,"银行卡"),
    CREDIT(5,"挂账");

    private Integer type;
    private String desc;

    OrderPayType(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }
}

