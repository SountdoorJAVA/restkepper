package com.restkeeper.shop.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restkeeper.shop.entity.Brand;
import com.restkeeper.shop.mapper.BrandMapper;
import org.apache.dubbo.config.annotation.Service;

@Service(version = "1.0.0",protocol = "dubbo")
public class BrandServiceImpl extends ServiceImpl<BrandMapper, Brand> implements IBrandService {
}
