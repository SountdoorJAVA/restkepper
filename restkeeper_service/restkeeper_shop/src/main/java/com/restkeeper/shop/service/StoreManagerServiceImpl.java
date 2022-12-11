package com.restkeeper.shop.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restkeeper.shop.entity.StoreManager;
import com.restkeeper.shop.mapper.StoreManagerMapper;
import org.apache.dubbo.config.annotation.Service;

@Service(version = "1.0.0",protocol = "dubbo")
public class StoreManagerServiceImpl extends ServiceImpl<StoreManagerMapper, StoreManager> implements IStoreManagerService {
}
