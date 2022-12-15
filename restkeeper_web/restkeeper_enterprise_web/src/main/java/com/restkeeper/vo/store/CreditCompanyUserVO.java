package com.restkeeper.vo.store;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 挂账公司用户VO
 */
@Data
public class CreditCompanyUserVO{

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "手机号码")
    private String phone;
}
