package com.restkeeper.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.restkeeper.entity.OrderDetailAllView;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderDetailAllMapper extends BaseMapper<OrderDetailAllView> {
}
