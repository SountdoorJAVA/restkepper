package com.restkeeper.vo.store;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 门店设置vo
 */
@Data
public class SettingsVO {
    @ApiModelProperty(value = "备注列表")
    private List<RemarkVO> remarks;

}
