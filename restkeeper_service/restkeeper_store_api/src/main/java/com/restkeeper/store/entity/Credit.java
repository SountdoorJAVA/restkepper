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
import java.util.List;

/**
 * 挂账单位管理
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Credit", description="挂账管理表")
@TableName(value = "t_credit")
public class Credit extends BaseStoreEntity implements Serializable{
    @TableId(value = "credit_id",type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private String creditId;

    @ApiModelProperty(value = "挂账类型 1 公司 2 个人")
    private Integer creditType;

    @ApiModelProperty(value = "公司名称,如果个人可以为空")
    private String companyName;

    @ApiModelProperty(value = "用户名称")
    private String userName;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "状态 1 开启 0 停用")
    private Integer status;

    @ApiModelProperty(value = "挂账金额")
    private Integer creditAmount;

    @ApiModelProperty(value = "总还款金额")
    private Integer totalRepaymentAmount;

    @ApiModelProperty(value = "单位公司用户列表")
    @TableField(exist = false)
    private List<CreditCompanyUser> users;

}
