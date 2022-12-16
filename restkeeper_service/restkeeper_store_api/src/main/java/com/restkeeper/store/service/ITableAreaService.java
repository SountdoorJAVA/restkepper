package com.restkeeper.store.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.restkeeper.store.entity.TableArea;

import java.util.List;
import java.util.Map;

public interface ITableAreaService extends IService<TableArea> {
    boolean add(TableArea tableArea);

    boolean update(String id, String name);

    List<Map<String, Object>> listTableArea();
}
