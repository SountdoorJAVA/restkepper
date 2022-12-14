package com.restkeeper.store.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.restkeeper.store.entity.TableArea;

public interface ITableAreaService extends IService<TableArea> {
    boolean add(TableArea tableArea);

    boolean update(String id, String name);
}
