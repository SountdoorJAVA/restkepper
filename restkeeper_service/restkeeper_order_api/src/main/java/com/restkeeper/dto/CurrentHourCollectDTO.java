package com.restkeeper.dto;

import lombok.Data;

@Data
public class CurrentHourCollectDTO{
    /**
     * 销售数据
     */
    private Integer totalAmount;
    /**
     * 24小时值
     */
    private Integer currentDateHour;
}
