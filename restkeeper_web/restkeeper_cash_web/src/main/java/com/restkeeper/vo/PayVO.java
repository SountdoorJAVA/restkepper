package com.restkeeper.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PayVO {

    @ApiModelProperty(value = "支付方式")
    private Integer payType; //付款方式 0 现金 1 微信 2 支付宝 3 挂账 4 免单

    @ApiModelProperty(value = "实付金额")
    private Integer payAmount; //实付金额

    @ApiModelProperty(value = "抹零金额")
    private Integer smallAmount; //抹零金额"

    @ApiModelProperty(value = "挂账id")
    private String creditId; //挂账id

    @ApiModelProperty(value = "挂账金额")
    private Integer creditAmount; //挂账金额

    @ApiModelProperty(value = "挂账人")
    private String creditUserName; //挂账人
}
