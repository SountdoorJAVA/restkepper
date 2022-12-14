package com.restkeeper.controller.store;

import com.restkeeper.store.entity.TableArea;
import com.restkeeper.store.service.ITableAreaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;

/**
 * @author MORRIS --> Java
 * @date 2022-12-15 02:37:10
 */
@Slf4j
@Api(tags = {"区域管理"})
@RestController
@RequestMapping("/table")
public class TableAreaController {
    @Reference(version = "1.0.0", check = false)
    private ITableAreaService tableAreaService;

    @ApiOperation(value = "添加区域")
    @PostMapping("/addArea")
    public boolean addArea(@RequestParam("name") String name) {
        TableArea tableArea = new TableArea();
        tableArea.setAreaName(name);
        return tableAreaService.add(tableArea);
    }

    @ApiOperation(value = "更新区域")
    @PostMapping("/updateArea/{id}")
    public boolean updateArea(@PathVariable String id, @RequestParam("name") String name) {
        return tableAreaService.update(id, name);
    }
}
