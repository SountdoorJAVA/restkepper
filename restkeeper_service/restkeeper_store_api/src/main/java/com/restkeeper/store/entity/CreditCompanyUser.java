package com.restkeeper.store.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 挂账单位人员
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="CreditCompanyUser", description="挂账公司人员")
@TableName(value = "t_credit_company_user")
public class CreditCompanyUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @TableId(value = "id",type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty(value = "挂账公司关联id")
    private String creditId;

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "最后更新时间")
    @TableField(value = "last_update_time",fill = FieldFill.INSERT_UPDATE)
    protected LocalDateTime lastUpdateTime;

    @ApiModelProperty(value = "所属商户id")
    @TableField(value = "shop_id")
    protected String shopId;

    @ApiModelProperty(value = "门店id")
    protected String storeId;
}
