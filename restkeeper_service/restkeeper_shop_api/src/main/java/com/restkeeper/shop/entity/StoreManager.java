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
import java.util.List;

/**
 * <p>
 * 门店管理员信息
 * </p>
 * @since 2019-11-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "t_store_manager",resultMap = "BaseResultMap")
@ApiModel(value="StoreManager对象", description="门店管理员信息")
public class StoreManager extends BaseShopEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(type = IdType.ASSIGN_ID)
    private String storeManagerId;

    @ApiModelProperty(value = "门店管理员姓名")
    private String storeManagerName;

    @ApiModelProperty(value = "门店管理员电话")
    private String storeManagerPhone;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @JsonIgnore
    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "管理的门店集合")
    @TableField(exist = false)
    private List<Store> stores;

}
