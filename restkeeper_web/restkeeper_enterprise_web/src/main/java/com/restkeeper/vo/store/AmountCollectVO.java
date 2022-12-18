package com.restkeeper.vo.store;

import lombok.Data;

@Data
public class AmountCollectVO {


    private static final long serialVersionUID = 1L;
    /**
     * 已付款总金额
     */
    private Integer payTotal;

    /**
     * 已付款订单总数
     */
    private Integer payTotalCount;

    /**
     * 未付款总金额
     */
    private Integer noPayTotal;

    /**
     * 未付款订单总数
     */
    private Integer noPayTotalCount;

    /**
     * 就餐总人数
     */
    private Integer totalPerson;

    /**
     * 正在就餐的人数
     */
    private Integer currentPerson;
}
