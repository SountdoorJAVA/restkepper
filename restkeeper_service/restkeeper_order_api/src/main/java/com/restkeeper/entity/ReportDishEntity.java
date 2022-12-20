package com.restkeeper.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName(value = "t_report_dish")
public class ReportDishEntity extends  BaseEntity  implements Serializable {
    @TableId(value = "id",type = IdType.ASSIGN_ID)
    private Long id;
    private LocalDate payDate;
    private String category;
    private String dishName;
    private Integer dishNumber;
    private Integer dishMoney;
}
