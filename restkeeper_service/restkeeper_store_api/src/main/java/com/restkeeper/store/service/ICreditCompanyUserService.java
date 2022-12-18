package com.restkeeper.store.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.restkeeper.store.entity.CreditCompanyUser;

import java.util.List;

public interface ICreditCompanyUserService extends IService<CreditCompanyUser> {
    List<CreditCompanyUser> getInfoList(String creditId);
}
