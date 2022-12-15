package com.restkeeper.vo.store;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 挂账管理VO
 */
@Data
public class CreditVO {

    @ApiModelProperty(value = "挂账类型 1 企业 2 个人")
    private int creditType;

    @ApiModelProperty(value = "挂账公司名")
    private String companyName;

    @ApiModelProperty(value = "挂账公司用户列表")
    private List<CreditCompanyUserVO> users;

    //挂账类型个人时候使用
    @ApiModelProperty(value = "个人类型 挂账人")
    private String userName;

    @ApiModelProperty(value = "个人类型 联系电话")
    private String phone;

    //回显使用
    @ApiModelProperty(value = "挂账用户列表")
    private Integer creditAmount; //挂账金额

    @ApiModelProperty(value = "挂账用户列表")
    private Integer status=1; // 状态 0停用， 1启用
}