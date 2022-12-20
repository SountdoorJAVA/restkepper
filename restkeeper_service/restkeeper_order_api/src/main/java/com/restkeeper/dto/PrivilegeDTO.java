package com.restkeeper.dto;

import lombok.Data;

@Data
public class PrivilegeDTO{
    /**
     * 赠菜金额
     */
    private int presentAmount;
    /**
     * 免单金额
     */
    private int freeAmount;
    /**
     * 抹零金额
     */
    private int smallAmount;
}
