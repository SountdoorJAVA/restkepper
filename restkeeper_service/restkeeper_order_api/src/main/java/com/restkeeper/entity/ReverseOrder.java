package com.restkeeper.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 反结账主表
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ReverseOrder对象", description="反结账主表")
@TableName(value = "t_reverse_order")
public class ReverseOrder extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    private String id;

    @ApiModelProperty(value = "关联订单号")
    private String orderId;

    @ApiModelProperty(value = "流水号")
    private String orderNumber;

    @ApiModelProperty(value = "桌台id")
    private String tableId;

    @ApiModelProperty(value = "反结账状态0 失败 1 成功")
    private Integer status;

    @ApiModelProperty(value = "反结账金额")
    private Integer amount;

    @ApiModelProperty(value = "反结账备注")
    private String remark;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "操作人名称")
    private String operatorName;


}
