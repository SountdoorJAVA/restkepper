package com.restkeeper.store.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 桌台记录
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="TableLog对象", description="桌台记录")
@TableName(value="t_table_log")
public class TableLog extends BaseStoreEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "开桌记录id")
    @TableId(type = IdType.ASSIGN_ID)
    private String logId;

    @ApiModelProperty(value = "桌台id")
    private String tableId;

    @ApiModelProperty(value = "操作人")
    private String userId;

    @ApiModelProperty(value = "桌台操作记录 1 开桌 2 锁桌 ")
    private Integer tableStatus;

    @ApiModelProperty(value = "用餐人数")
    private Integer userNumbers;

    @ApiModelProperty(value = "开桌时间")
    private LocalDateTime createTime;


}

