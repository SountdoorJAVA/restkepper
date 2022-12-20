package com.restkeeper.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("t_report_dish")
public class ReportDish{
    @TableId(value = "id",type = IdType.ASSIGN_ID)
    private Long id;
    private LocalDate payDate;
    private String category;
    private String dishName;
    //销售量
    private Integer dishNumber;

    //销售额
    private Integer dishMoney;
    private String shopId;
    private String storeId;
    @TableField(value = "is_deleted")
    private Integer isDeleted;
    @TableField(value = "last_update_time")
    protected LocalDateTime lastUpdateTime;
}
