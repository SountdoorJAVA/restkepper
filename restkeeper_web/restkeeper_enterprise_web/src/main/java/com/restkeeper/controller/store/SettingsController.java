package com.restkeeper.controller.store;

import com.alibaba.nacos.client.utils.StringUtils;
import com.google.common.collect.Lists;
import com.restkeeper.store.entity.Remark;
import com.restkeeper.store.service.IRemarkService;
import com.restkeeper.vo.store.RemarkVO;
import com.restkeeper.vo.store.SettingsVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author MORRIS --> Java
 * @date 2022-12-15 18:03:00
 */
@Slf4j
@Api(tags = {"门店备注管理"})
@RestController
@RequestMapping("/settings")
public class SettingsController {
    @Reference(version = "1.0.0", check = false)
    private IRemarkService remarkService;

    @ApiOperation(value = "获取门店设置信息")
    @GetMapping("/getSysSettings")
    public SettingsVO getSysSettings() {
        //该方法会先到备注表查询,没有的话,在查询字典表默认的备注
        List<Remark> remarks = remarkService.getRemarks();
        List<RemarkVO> remarkvos = new ArrayList<>();
        remarks.forEach(remark -> {
            val remarkVO = new RemarkVO();
            String remarkValue = remark.getRemarkValue();
            val substring = remarkValue.substring(remarkValue.indexOf("[") + 1, remarkValue.indexOf("]"));
            if (StringUtils.isNotEmpty(substring)) {
                String[] remark_array = substring.split(",");
                remarkVO.setRemarkValue(Arrays.asList(remark_array));
            }
            remarkVO.setRemarkName(remark.getRemarkName());
            remarkvos.add(remarkVO);
        });

        SettingsVO settingsVO = new SettingsVO();
        settingsVO.setRemarks(remarkvos);
        return settingsVO;
    }

    @ApiOperation(value = "修改门店设置")
    @PutMapping("/update")
    public boolean update(@RequestBody SettingsVO settingsVO) {
        List<RemarkVO> remarkVOList = settingsVO.getRemarks();
        List<Remark> remarkList = new ArrayList<>();
        remarkVOList.forEach(remarkVO -> {
            val remark = new Remark();
            remark.setRemarkName(remarkVO.getRemarkName());
            remark.setRemarkValue(remarkVO.getRemarkValue().toString());
            remarkList.add(remark);
        });
        return remarkService.updateRemarks(remarkList);
    }
}
