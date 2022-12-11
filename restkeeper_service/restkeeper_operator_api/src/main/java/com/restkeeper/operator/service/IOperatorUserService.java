package com.restkeeper.operator.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.restkeeper.operator.entity.OperatorUser;
import com.restkeeper.utils.Result;

public interface IOperatorUserService extends IService<OperatorUser> {

    IPage<OperatorUser> queryPageByName(int pageNum, int pageSize, String name);

    Result login(String loginName, String loginPass);

}
