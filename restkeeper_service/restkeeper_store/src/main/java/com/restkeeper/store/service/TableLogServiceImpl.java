package com.restkeeper.store.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restkeeper.constants.SystemCode;
import com.restkeeper.exception.BussinessException;
import com.restkeeper.store.entity.Table;
import com.restkeeper.store.entity.TableLog;
import com.restkeeper.store.mapper.TableLogMapper;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Service;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.time.LocalDateTime;

@org.springframework.stereotype.Service("tableLogService")
@Service(version = "1.0.0", protocol = "dubbo")
public class TableLogServiceImpl extends ServiceImpl<TableLogMapper, TableLog> implements ITableLogService {
    @Autowired
    @Qualifier("tableService")
    private ITableService tableService;

    @Autowired
    @Qualifier("tableLogService")
    private ITableLogService tableLogService;

    @Override
    @Transactional
    public boolean openTable(TableLog tableLog) {
        //获取当前桌台信息
        Table table = tableService.getById(tableLog.getTableId());
        //如果为非空闲，不能开桌
        if (SystemCode.TABLE_STATUS_FREE != table.getStatus()) {
            throw new BussinessException("非空闲桌台，不能开桌");
        }
        //就餐人数是否超过桌台限制
        if (tableLog.getUserNumbers() > table.getTableSeatNumber()) {
            throw new BussinessException("超过桌台人数限制，不能开桌");
        }
        //修改桌台状态
        table.setStatus(SystemCode.TABLE_STATUS_OPEND);
        tableService.updateById(table);
        //设置开桌人和时间
        tableLog.setUserId(RpcContext.getContext().getAttachment("loginUserName"));
        tableLog.setCreateTime(LocalDateTime.now());
        tableLog.setTableStatus(SystemCode.TABLE_STATUS_LOCKED);
        return this.save(tableLog);
    }

    @Override
    public TableLog getOpenTableLog(String tableId) {
        QueryWrapper<TableLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(TableLog::getTableId, tableId)
                .orderByDesc(TableLog::getCreateTime);
        return this.list(queryWrapper).get(0);
    }

    @ApiOperation(value = "开桌")
    @PutMapping("/openTable/{tableId}/{numbers}")
    public boolean openTable(@PathVariable String tableId, @PathVariable Integer numbers) {
        TableLog tableLog = new TableLog();
        tableLog.setTableId(tableId);
        tableLog.setUserNumbers(numbers);
        return tableLogService.openTable(tableLog);
    }
}
