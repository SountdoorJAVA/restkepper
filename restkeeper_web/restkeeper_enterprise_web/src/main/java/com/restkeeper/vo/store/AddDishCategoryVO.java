package com.restkeeper.vo.store;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AddDishCategoryVO {

     @ApiModelProperty(value = "分类名称")
    private String categoryName;

    @ApiModelProperty(value = "分类类型")
    private int type;

}
