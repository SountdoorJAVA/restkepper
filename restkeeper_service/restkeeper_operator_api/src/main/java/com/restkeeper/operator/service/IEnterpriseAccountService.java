package com.restkeeper.operator.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.restkeeper.operator.entity.EnterpriseAccount;

/**
 * @author MORRIS --> Java
 * @date 2022-12-11 16:23:03
 */
public interface IEnterpriseAccountService extends IService<EnterpriseAccount> {

    //根据名称分页查询
    IPage<EnterpriseAccount> queryPageByName(int pageNum, int pageSize, String enterpriseName);

    //新增帐号
    boolean add(EnterpriseAccount account);

    //账号还原
    boolean recovery(String id);

    //重置密码
    boolean resetPwd(String id, String password);
}
