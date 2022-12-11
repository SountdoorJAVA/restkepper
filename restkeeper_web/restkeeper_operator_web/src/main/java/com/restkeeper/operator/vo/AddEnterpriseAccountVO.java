package com.restkeeper.operator.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AddEnterpriseAccountVO {

    @ApiModelProperty(value = "企业名称")
    private String enterpriseName;

    @ApiModelProperty(value = "申请人")
    private String applicant;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "省")
    private String province;

    @ApiModelProperty(value = "市")
    private String city;

    @ApiModelProperty(value = "区")
    private String area;

    @ApiModelProperty(value = "详细地址")
    private String address;

    @ApiModelProperty(value = "状态(试用中0,正式1,停用-1)")
    private Integer status;

    @ApiModelProperty(value = "当正式期时候，需要设置有效期 前端转换成（天）传给后端")
    private int validityDay;
}
