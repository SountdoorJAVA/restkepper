package com.restkeeper.shop.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.restkeeper.shop.entity.Brand;

import java.util.List;
import java.util.Map;

public interface IBrandService extends IService<Brand> {
    //分页降序查询
    IPage<Brand> queryPage(int pageNo, int pageSize);

    //查询所有品牌id和对应name
    List<Map<String, Object>> getBrandList();

}
