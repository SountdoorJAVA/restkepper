package com.restkeeper.store.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restkeeper.exception.BussinessException;
import com.restkeeper.store.entity.TableArea;
import com.restkeeper.store.mapper.TableAreaMapper;
import lombok.val;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.transaction.annotation.Transactional;

@org.springframework.stereotype.Service("tableAreaService")
@Service(version = "1.0.0", protocol = "dubbo")
public class TableAreaServiceImpl extends ServiceImpl<TableAreaMapper, TableArea> implements ITableAreaService {
    @Override
    @Transactional
    public boolean add(TableArea tableArea) {
        //区域名称防重
        checkNameExist(tableArea.getAreaName());
        return this.save(tableArea);
    }

    @Override
    @Transactional
    public boolean update(String id, String name) {
        //获取区域
        val tableArea = this.getById(id);
        //校验名称
        checkNameExist(name);
        //设置新名称
        tableArea.setAreaName(name);
        //更新区域
        return this.updateById(tableArea);
    }

    private void checkNameExist(String areaName) {
        QueryWrapper<TableArea> qw = new QueryWrapper<>();
        qw.lambda().select(TableArea::getAreaId).eq(TableArea::getAreaName, areaName);
        val count = this.baseMapper.selectCount(qw);
        if (count > 0) {
            throw new BussinessException("该区域已存在");
        }
    }
}
