package com.restkeeper.store.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="CreditRepayment", description="还款记录")
@TableName(value = "t_credit_repayment")
public class CreditRepayment extends BaseStoreEntity implements Serializable {

    @ApiModelProperty(value = "日志Id")
    @TableId(value = "log_id",type = IdType.ASSIGN_ID)
    private String logId;

    @ApiModelProperty(value = "挂账类型,1公司;2个人")
    @TableField(value = "credit_type")
    private String creditType;

    @ApiModelProperty(value = "挂账管理id")
    @TableField(value = "credit_id")
    private String creditId;

    @ApiModelProperty(value = "挂账公司名称")
    @TableField(value = "company_name")
    private String companyName;

    @ApiModelProperty(value = "还款人名称")
    @TableField(value = "user_name")
    private String userName;

    @ApiModelProperty(value = "还款方式")
    @TableField(value = "repayment_type")
    private String repaymentType;

    @ApiModelProperty(value = "还款金额")
    @TableField(value = "repayment_amount")
    private Integer repaymentAmount;
}

