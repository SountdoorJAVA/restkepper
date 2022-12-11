package com.restkeeper.operator.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.restkeeper.operator.entity.EnterpriseAccount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * @author MORRIS --> Java
 * @date 2022-12-11 16:22:16
 */
@Mapper
public interface EnterpriseAccountMapper extends BaseMapper<EnterpriseAccount> {
    /**企业账号数据还原*/
    @Update("update t_enterprise_account set is_deleted=0 where enterprise_id=#{id} and is_deleted=1")
    boolean recovery(@Param("id") String id);
}
