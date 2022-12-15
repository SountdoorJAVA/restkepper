package com.restkeeper.store.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restkeeper.store.entity.CreditCompanyUser;
import com.restkeeper.store.mapper.CreditCompanyUserMapper;
import org.apache.dubbo.config.annotation.Service;

@org.springframework.stereotype.Service("creditCompanyUserService")
@Service(version = "1.0.0",protocol = "dubbo")
public class CreditCompanyUserServiceImpl extends ServiceImpl<CreditCompanyUserMapper, CreditCompanyUser> implements ICreditCompanyUserService {
}
