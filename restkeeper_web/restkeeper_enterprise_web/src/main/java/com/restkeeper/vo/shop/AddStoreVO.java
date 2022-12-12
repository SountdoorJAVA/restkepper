package com.restkeeper.vo.shop;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AddStoreVO {

    @ApiModelProperty(value = "门店名称")
    private String storeName;

    @ApiModelProperty(value = "所属品牌")
    private String brandId;

    @ApiModelProperty(value = "省")
    private String province;

    @ApiModelProperty(value = "市")
    private String city;

    @ApiModelProperty(value = "区")
    private String area;

    @ApiModelProperty(value = "详细地址")
    private String address;

    @ApiModelProperty(value = "联系人")
    private String contact;

    @ApiModelProperty(value = "联系人电话")
    private String contactPhone;
}
