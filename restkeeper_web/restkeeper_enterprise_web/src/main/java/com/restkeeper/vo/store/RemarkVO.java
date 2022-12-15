package com.restkeeper.vo.store;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 备注vo
 */
@Data
public class RemarkVO {

    @ApiModelProperty(value = "备注名称")
    private String remarkName;

    @ApiModelProperty(value = "备注集合")
    private List<String> remarkValue;

}
