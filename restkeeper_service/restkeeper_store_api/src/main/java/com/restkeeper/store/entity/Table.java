package com.restkeeper.store.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Table", description="桌台")
@TableName(value = "t_table",resultMap = "BaseResultMap")
public class Table extends BaseStoreEntity implements Serializable {

    @TableId(value = "table_id",type = IdType.ASSIGN_ID)
    private String tableId;

    @TableField(value = "area_id")
    private String areaId;

    @TableField(value = "table_name")
    private String tableName;

    @TableField(value = "table_seat_number")
    private Integer tableSeatNumber;

    @TableField(value = "status") // 0空闲 1 开桌 2 锁桌
    private Integer status;

    @TableField(exist = false)
    private TableArea area;

}
