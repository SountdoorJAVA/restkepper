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
@ApiModel(value="CreditLogs", description="挂账日志")
@TableName(value = "t_credit_logs")
public class CreditLogs extends BaseStoreEntity implements Serializable{
    @ApiModelProperty(value = "日志Id")
    @TableId(value = "log_id",type = IdType.ASSIGN_ID)
    private String logId;
    @ApiModelProperty(value = "订单Id")
    @TableField(value = "order_id")
    private String orderId;
    @ApiModelProperty(value = "挂账管理Id")
    @TableField(value = "credit_id")
    private String creditId;
    @TableField(value = "credit_type")
    private Integer type;
    @ApiModelProperty(value = "挂账公司名称")
    @TableField(value = "company_name")
    private String companyName;
    @ApiModelProperty(value = "挂账人名称")
    @TableField(value = "user_name")
    private String userName;
    @TableField(value = "phone")
    private String phone;
    @ApiModelProperty(value = "订单金额")
    @TableField(value = "order_amount")
    private Integer orderAmount;
    @ApiModelProperty(value = "应收金额")
    @TableField(value = "received_amount")
    private Integer receivedAmount;
    @ApiModelProperty(value = "应收金额")
    @TableField(value = "credit_amount")
    private Integer creditAmount;
}
