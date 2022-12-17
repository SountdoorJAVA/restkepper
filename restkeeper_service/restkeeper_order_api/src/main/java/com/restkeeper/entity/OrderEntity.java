package com.restkeeper.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 订单主表
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value="Order对象", description="订单主表")
@TableName(value = "t_order")
@NoArgsConstructor
public class OrderEntity extends  BaseEntity  implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "order_id",type = IdType.ASSIGN_ID)
    private String orderId;

    @ApiModelProperty(value = "流水号")
    private String orderNumber;

    @ApiModelProperty(value = "桌台id")
    private String tableId;

    @ApiModelProperty(value = "付款方式 0 免单 1 现金 2 微信 3 支付宝  4 银行卡 5 挂账")
    private Integer payType;

    @ApiModelProperty(value = "支付状态 0 未付 1 已付 ")
    private Integer payStatus;

    @ApiModelProperty(value = "付款金额")
    private Integer payAmount;

    @ApiModelProperty(value = "就餐人数")
    private Integer smallAmount; //抹零金额

    @ApiModelProperty(value = "就餐人数")
    private Integer personNumbers; //就餐人数

    @ApiModelProperty(value = "应收金额")
    private Integer totalAmount;

    @ApiModelProperty(value = "赠菜金额")
    private Integer presentAmount;

    @ApiModelProperty(value = "免单金额")
    private Integer freeAmount;

    @ApiModelProperty(value = "整单备注")
    private String orderRemark;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "操作人")
    private String operatorName;

    @ApiModelProperty(value = "购买人id")
    private String buyId;

    @ApiModelProperty(value = "订单来源")
    private Integer orderSource; //订单来源 0 收银端 1 app

    @ApiModelProperty(value = "订单详情列表")
    @TableField(exist = false)
    private List<OrderDetailEntity> orderDetails;

    @TableField(exist = false)
    private Integer currentDateHour;
}

