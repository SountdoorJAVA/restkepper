package com.restkeeper.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.restkeeper.constants.SystemCode;
import com.restkeeper.response.vo.PageVO;
import com.restkeeper.store.entity.Table;
import com.restkeeper.store.entity.TableLog;
import com.restkeeper.store.service.ITableAreaService;
import com.restkeeper.store.service.ITableLogService;
import com.restkeeper.store.service.ITableService;
import com.restkeeper.vo.TablePanelVO;
import com.restkeeper.vo.TableVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author MORRIS --> Java
 * @date 2022-12-17 00:01:21
 */
@RestController
@RequestMapping("/table")
@Api(tags = {"收银端区域桌台接口"})
public class TableController {
    @Reference(version = "1.0.0", check = false)
    private ITableAreaService tableAreaService;
    @Reference(version = "1.0.0", check = false)
    private ITableService tableService;
    @Reference(version = "1.0.0", check = false)
    private ITableLogService tableLogService;

    @ApiOperation(value = "区域列表接口")
    @GetMapping("/listTableArea")
    public List<Map<String, Object>> list() {
        return tableAreaService.listTableArea();
    }

    @ApiOperation(value = "桌台面板")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "areaId", value = "区域Id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "path", name = "page", value = "页码", required = true, dataType = "Integer"),
            @ApiImplicitParam(paramType = "path", name = "pageSize", value = "每页数量", required = true, dataType = "Integer")})
    @GetMapping("/search/{areaId}/{page}/{pageSize}")
    public TablePanelVO queryByArea(@PathVariable String areaId,
                                    @PathVariable int page,
                                    @PathVariable int pageSize) {
        TablePanelVO tablePanelVO = new TablePanelVO();
        //查询桌台状态数量信息
        tablePanelVO.setFreeNumbers(tableService.countTableByStatus(areaId, SystemCode.TABLE_STATUS_FREE));
        tablePanelVO.setLockedNumbers(tableService.countTableByStatus(areaId, SystemCode.TABLE_STATUS_LOCKED));
        tablePanelVO.setOpenedNumbers(tableService.countTableByStatus(areaId, SystemCode.TABLE_STATUS_OPEND));

        //查询当前区域桌台分页信息
        IPage<Table> tableIPage = tableService.queryPageByAreaId(areaId, page, pageSize);
        List<TableVO> tableVOList = new ArrayList<>();
        tableIPage.getRecords().forEach(table -> {
            TableVO tableVO = new TableVO();
            tableVO.setTableId(table.getTableId());
            tableVO.setTableName(table.getTableName());
            //如果是开台,需要查询桌台日志表最新记录获取创建时间和用餐人数
            if (table.getStatus() == SystemCode.TABLE_STATUS_OPEND) {
                TableLog openTableLog = tableLogService.getOpenTableLog(table.getTableId());
                tableVO.setUserNumbers(openTableLog.getUserNumbers());
                tableVO.setCreateTime(openTableLog.getCreateTime());
            }
            tableVOList.add(tableVO);
        });

        PageVO<TableVO> tableVOPageVO = new PageVO<>(tableIPage, tableVOList);
        tablePanelVO.setTablePage(tableVOPageVO);
        return tablePanelVO;

    }
}
