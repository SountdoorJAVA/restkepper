package com.restkeeper.store.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.restkeeper.store.entity.Staff;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface StaffMapper extends BaseMapper<Staff> {
    @Select("select * from t_staff where shop_id=#{shopId} and staff_name=#{loginName}")
    Staff login(@Param("shopId") String shopId, @Param("loginName") String loginName);
}
