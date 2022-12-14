package com.restkeeper.store.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restkeeper.exception.BussinessException;
import com.restkeeper.store.entity.Table;
import com.restkeeper.store.mapper.TableMapper;
import lombok.val;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.transaction.annotation.Transactional;

@org.springframework.stereotype.Service("tableService")
@Service(version = "1.0.0", protocol = "dubbo")
public class TableServiceImpl extends ServiceImpl<TableMapper, Table> implements ITableService {
    @Override
    @Transactional
    public boolean add(Table table) {
        checkNameExist(table.getTableName());
        return this.save(table);
    }

    @Override
    public IPage<Table> queryPageByAreaId(String areaId, int pageNum, int pageSize) {
        IPage<Table> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Table> qw = new QueryWrapper<>();
        if (!areaId.equals("all")) {
            qw.lambda().eq(Table::getAreaId, areaId).orderByDesc(Table::getLastUpdateTime);
        }
        return this.page(page, qw);
    }

    private void checkNameExist(String tableName) {
        QueryWrapper<Table> qw = new QueryWrapper<>();
        qw.lambda().select(Table::getTableId).eq(Table::getTableName, tableName);
        val count = this.baseMapper.selectCount(qw);
        if (count > 0) {
            throw new BussinessException("该桌台已存在");
        }
    }
}
