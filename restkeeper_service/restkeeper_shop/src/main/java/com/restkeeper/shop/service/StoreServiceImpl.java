package com.restkeeper.shop.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restkeeper.shop.dto.StoreDTO;
import com.restkeeper.shop.entity.Store;
import com.restkeeper.shop.mapper.StoreMapper;
import com.restkeeper.utils.BeanListUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.Service;

import java.util.ArrayList;
import java.util.List;

@Service(version = "1.0.0", protocol = "dubbo")
@org.springframework.stereotype.Service("storeService")
@Slf4j
public class StoreServiceImpl extends ServiceImpl<StoreMapper, Store> implements IStoreService {
    @Override
    public IPage<Store> queryPageByName(int pageNo, int pageSize, String name) {
        //分页查询
        IPage<Store> page = new Page<>(pageNo, pageSize);
        //条件包装
        QueryWrapper<Store> qw = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(name)) {
            qw.lambda().like(Store::getStoreName, name);
        }
        return this.page(page, qw);
    }

    @Override
    public List<String> getAllProvince() {
        return getBaseMapper().getAllProvince();
    }

    @Override
    public List<StoreDTO> getStoreByProvince(String province) {
        //通过省份查询该省份下营业的门店
        QueryWrapper<Store> queryWrapper = new QueryWrapper<Store>();
        queryWrapper.lambda().eq(Store::getStatus, 1);
        if (!StringUtils.isEmpty(province) && !"all".equalsIgnoreCase(province)) {
            queryWrapper.lambda().eq(Store::getProvince, province);
        }
        List<Store> list = this.list(queryWrapper);

        List<StoreDTO> list_dto;
        try {
            return list_dto = BeanListUtils.copy(list, StoreDTO.class);
        } catch (Exception e) {
            log.info("转换出错");
        }
        return new ArrayList<StoreDTO>();
    }
}
