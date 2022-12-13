package com.restkeeper.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 门店信息账号
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Store对象", description="门店信息账号")
@TableName(value = "t_store",resultMap = "BaseResultMap")
public class Store extends BaseShopEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "门店主键id")
    @TableId(type = IdType.ASSIGN_ID)
    private String storeId;

    @ApiModelProperty(value = "门店名称")
    private String storeName;

    @JsonIgnore
    @ApiModelProperty(value = "所属品牌")
    private String brandId;

    @ApiModelProperty(value = "省")
    private String province;

    @ApiModelProperty(value = "市")
    private String city;

    @ApiModelProperty(value = "区")
    private String area;

    @ApiModelProperty(value = "联系人")
    private String contact;

    @ApiModelProperty(value = "联系人电话")
    private String contactPhone;

    @ApiModelProperty(value = "门店管理员id")
    private String storeManagerId;

    @ApiModelProperty(value = "地址")
    private String address;

    @ApiModelProperty(value = "状态(正式运营1，停业0)")
    private Integer status;

    @ApiModelProperty(value = "所属品牌对象")
    @TableField(exist = false)
    private Brand brand;
}
