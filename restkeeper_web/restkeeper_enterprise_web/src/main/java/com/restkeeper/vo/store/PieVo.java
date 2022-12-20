package com.restkeeper.vo.store;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PieVo {
    @ApiModelProperty(value = "支付类型")
    private String name;
    @ApiModelProperty(value = "支付总额")
    private Integer value;
    @ApiModelProperty(value = "占比")
    private Double percent;
}
