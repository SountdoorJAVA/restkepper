package com.restkeeper.vo.shop;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class StoreManagerVO {

    @ApiModelProperty(value = "所属商户Id")
    private String shopId;

    @ApiModelProperty(value = "id")
    private String id;
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "手机号")
    private String phone;
    @ApiModelProperty(value = "门店id列表")
    private List<String> storeIds;

}
