package com.restkeeper.order.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.restkeeper.entity.OrderDetailEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 订单详情表 Mapper 接口
 * </p>
 */
@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetailEntity> {

}

