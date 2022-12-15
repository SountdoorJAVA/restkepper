package com.restkeeper.store.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 备注信息
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Remarks对象", description="备注信息")
@TableName(value = "t_remark")
public class Remark implements Serializable {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "备注id")
    @TableId(type = IdType.ASSIGN_ID)
    private String remarkId;

    @ApiModelProperty(value = "备注名称 退菜原因 退单原因。。。")
    private String remarkName;

    @ApiModelProperty(value = "备注")
    private String remarkValue;

    @ApiModelProperty(value = "最后更新时间")
    @TableField(value = "last_update_time",fill = FieldFill.INSERT_UPDATE)
    protected LocalDateTime lastUpdateTime;

    @ApiModelProperty(value = "所属商户id")
    @TableField(value = "shop_id")
    protected String shopId;

    @ApiModelProperty(value = "门店id")
    protected String storeId;
}

