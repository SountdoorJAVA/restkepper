package com.restkeeper.shop.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class StoreDTO {

    @ApiModelProperty(value = "门店id")
    private String storeId;

    @ApiModelProperty(value = "门店名称")
    private String storeName;

    @ApiModelProperty(value = "门店管理员id")
    private String storeManagerId;

    @ApiModelProperty(value = "门店地址")
    private String address;
}

