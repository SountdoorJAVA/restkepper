package com.restkeeper.vo.shop;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AddTBrandVO {

    @ApiModelProperty(value = "品牌名称")
    private String brandName;

    @ApiModelProperty(value = "所属业态")
    private String category;

    @ApiModelProperty(value = "品牌Logo")
    private String logo;

    @ApiModelProperty(value = "负责人")
    private String contact;

    @ApiModelProperty(value = "联系方式")
    private String contactPhone;

    @ApiModelProperty(value = "描述信息")
    private String info;
}
