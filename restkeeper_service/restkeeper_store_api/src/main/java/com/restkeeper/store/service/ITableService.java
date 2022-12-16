package com.restkeeper.store.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.restkeeper.store.entity.Table;

public interface ITableService extends IService<Table> {
    //新增
    boolean add(Table table);

    IPage<Table> queryPageByAreaId(String areaId, int page, int pageSize);

    //根据区域ID和状态查询总数
    Integer countTableByStatus(String areaId,Integer status);
}
