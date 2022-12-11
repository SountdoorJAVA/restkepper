package com.restkeeper.shop.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BaseShopEntity {

    @ApiModelProperty(value = "最后更新时间")
    @TableField(value = "last_update_time",fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime lastUpdateTime;

    @JsonIgnore
    @ApiModelProperty(value = "是否删除 1是删除 0 未删除")
    @TableLogic
    @TableField(value = "is_deleted")
    private Integer isDeleted;

    @JsonIgnore
    @ApiModelProperty(value = "所属商户id")
    @TableField(value = "shop_id")
    private String shopId;

}
