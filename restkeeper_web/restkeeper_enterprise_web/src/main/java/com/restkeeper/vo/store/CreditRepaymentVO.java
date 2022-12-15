package com.restkeeper.vo.store;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 还款vo
 */
@Data
public class CreditRepaymentVO {

    @ApiModelProperty(value = "挂账类型,1公司;2个人")
    private String creditType;

    @ApiModelProperty(value = "挂账管理id")
    private String creditId;

    @ApiModelProperty(value = "挂账公司名称")
    private String companyName;

    @ApiModelProperty(value = "还款人名称")
    private String userName;

    @ApiModelProperty(value = "还款方式")
    private String repaymentType;

    @ApiModelProperty(value = "还款金额")
    private Integer repaymentAmount;
}

