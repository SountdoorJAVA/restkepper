package com.restkeeper.dto;

import lombok.Data;

@Data
public class PayTypeCollectDTO{
    /**
     * 支付类型数据库定义值
     */
    private int payType;
    /**
     * 支付方式名称
     */
    private String payName;
    /**
     * 收款笔数
     */
    private Integer totalCount;
}
