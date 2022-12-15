package com.restkeeper.store.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.restkeeper.store.entity.Credit;
import com.restkeeper.store.entity.CreditCompanyUser;

import java.util.List;

public interface ICreditService extends IService<Credit> {
    //新增挂账
    boolean add(Credit credit, List<CreditCompanyUser> users);
    //分页查询
    IPage<Credit> queryPage(int page, int size, String username);
    //回显
    Credit queryById(String id);
    //修改
    boolean updateInfo(Credit credit, List<CreditCompanyUser> users);

}
