package com.restkeeper.operator.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restkeeper.operator.entity.SysDictionary;
import com.restkeeper.operator.mapper.SysDictMapper;
import org.apache.dubbo.config.annotation.Service;

import java.util.List;

@org.springframework.stereotype.Service("sysDictionaryService")
@Service(version = "1.0.0", protocol = "dubbo")
public class SysDictServiceImpl extends ServiceImpl<SysDictMapper, SysDictionary> implements ISysDictService {

    @Override
    public List<SysDictionary> getDictionaryList(String category) {
        QueryWrapper<SysDictionary> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysDictionary::getCategory, category);
        return this.list(queryWrapper);
    }
}
