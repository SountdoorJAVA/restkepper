package com.restkeeper.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 收银端桌台对象
 */
@Data
public class TableVO {
    @ApiModelProperty(value = "桌台id")
    private String tableId;

    @ApiModelProperty(value = "桌台名称")
    private String tableName;

    @ApiModelProperty(value = "开桌时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "用餐人数")
    private Integer userNumbers;
}

