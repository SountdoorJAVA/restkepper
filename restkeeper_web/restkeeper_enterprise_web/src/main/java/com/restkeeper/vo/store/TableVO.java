package com.restkeeper.vo.store;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TableVO {

    @ApiModelProperty(value = "区域id")
    private String areaId;

    @ApiModelProperty(value = "桌台名称")
    private String tableName;

    @ApiModelProperty(value = "座位数")
    private int tableSeatNumber;

}
