package com.restkeeper.order.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.restkeeper.entity.OrderEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 订单主表 Mapper 接口
 * </p>
 */
@Mapper
public interface OrderMapper extends BaseMapper<OrderEntity> {

}

