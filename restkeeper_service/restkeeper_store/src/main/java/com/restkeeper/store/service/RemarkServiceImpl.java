package com.restkeeper.store.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restkeeper.constants.SystemCode;
import com.restkeeper.operator.service.ISysDictService;
import com.restkeeper.store.entity.Remark;
import com.restkeeper.operator.entity.SysDictionary;
import com.restkeeper.store.mapper.RemarkMapper;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@org.springframework.stereotype.Service("remarkService")
@Service(version = "1.0.0", protocol = "dubbo")
public class RemarkServiceImpl extends ServiceImpl<RemarkMapper, Remark> implements IRemarkService {

    @Reference(version = "1.0.0", check = false)
    private ISysDictService sysDictService;

    @Override
    public List<Remark> getRemarks() {
        //获取备注列表
        List<Remark> remarks = this.list();
        //如果门店没有设置过备注，直接返回系统默认设置备注信息
        if (remarks == null || remarks.isEmpty()) {
            remarks = new ArrayList<>();
            List<SysDictionary> list = sysDictService.getDictionaryList(SystemCode.DICTIONARY_REMARK);
            //将Dictionary转换成Remark
            for (SysDictionary dictionary : list) {
                Remark remark = new Remark();
                remark.setRemarkName(dictionary.getDictName());
                remark.setRemarkValue(dictionary.getDictData());
                remarks.add(remark);
            }
        }
        return remarks;
    }

    @Override
    @Transactional
    public boolean updateRemarks(List<Remark> remarks) {
        //物理删除以前门店
        QueryWrapper<Remark> wrapper = new QueryWrapper<>();
        this.remove(wrapper);
        //批量插入新的数据
        return this.saveBatch(remarks);
    }
}
