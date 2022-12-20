package com.restkeeper.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName(value = "t_report_pay")
public class ReportPay {
    @TableId(value = "id",type = IdType.ASSIGN_ID)
    private Long id;
    private LocalDate payDate;
    private Integer payType;
    /**
     * 应收金额
     */
    private Integer totalAmount;
    /**
     * 赠送金额
     */
    private Integer presentAmount;
    /**
     * 抹零金额
     */
    private Integer smallAmount;
    /**
     * 免单金额
     */
    private Integer freeAmount;
    /**
     * 实收金额
     */
    private Integer payAmount;
    /**
     * 就餐人数
     */
    private Integer personNumbers;
    /**
     * 交易笔数
     */
    private Integer payCount;
    private String shopId;
    private String storeId;
    @TableField(value = "is_deleted")
    private Integer isDeleted;
    @TableField(value = "last_update_time")
    protected LocalDateTime lastUpdateTime;
}
