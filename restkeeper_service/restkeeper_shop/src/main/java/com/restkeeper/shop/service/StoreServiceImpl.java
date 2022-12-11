package com.restkeeper.shop.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restkeeper.shop.entity.Store;
import com.restkeeper.shop.mapper.StoreMapper;
import org.apache.dubbo.config.annotation.Service;

@Service(version = "1.0.0",protocol = "dubbo")
public class StoreServiceImpl extends ServiceImpl<StoreMapper, Store> implements IStoreService {
}
