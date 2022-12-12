package com.restkeeper.shop.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restkeeper.shop.entity.BaseShopEntity;
import com.restkeeper.shop.entity.Brand;
import com.restkeeper.shop.mapper.BrandMapper;
import org.apache.dubbo.config.annotation.Service;

import java.util.List;
import java.util.Map;

@Service(version = "1.0.0", protocol = "dubbo")
public class BrandServiceImpl extends ServiceImpl<BrandMapper, Brand> implements IBrandService {
    @Override
    public IPage<Brand> queryPage(int pageNo, int pageSize) {
        IPage<Brand> page = new Page<>(pageNo, pageSize);
        QueryWrapper<Brand> qw = new QueryWrapper<>();
        qw.lambda().orderByDesc(BaseShopEntity::getLastUpdateTime);
        return this.page(page, qw);
    }

    @Override
    public List<Map<String, Object>> getBrandList() {
        /**
        //获取所有品牌集合
        List<Brand> brands = this.list();
        //存放品牌id和品牌名的集合
        List<Map<String, Object>> list = new ArrayList<>();
        //遍历品牌集合
        brands.stream().forEach(brand -> {
            HashMap<String, Object> map = new HashMap<>();
            map.put(brand.getBrandId(), brand.getBrandName());
            list.add(map);
        });
        return list;
         */
        QueryWrapper<Brand> qw = new QueryWrapper<>();
        qw.lambda().select(Brand::getBrandId,Brand::getBrandName);
        return this.listMaps(qw);
    }
}
