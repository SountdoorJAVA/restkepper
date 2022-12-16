package com.restkeeper.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class LoginVO{
    @ApiModelProperty(value = "商户号")
    private String shopId;
    @ApiModelProperty(value = "登录账号")
    private String loginName;
    @ApiModelProperty(value = "密码")
    private String loginPass;
}
