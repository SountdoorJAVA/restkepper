package com.restkeeper.store.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BaseStoreEntity {

    @ApiModelProperty(value = "最后更新时间")
    @TableField(value = "last_update_time",fill = FieldFill.INSERT_UPDATE)
    protected LocalDateTime lastUpdateTime;

    @JsonIgnore
    @ApiModelProperty(value = "是否删除 1是删除 0 未删除")
    @TableLogic
    @TableField(value = "is_deleted")
    protected Integer isDeleted;

    @ApiModelProperty(value = "所属商户id")
    @TableField(value = "shop_id")
    protected String shopId;

    @ApiModelProperty(value = "门店id")
    protected String storeId;

}

