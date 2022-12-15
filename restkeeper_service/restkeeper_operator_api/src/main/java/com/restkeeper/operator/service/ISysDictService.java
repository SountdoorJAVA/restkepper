package com.restkeeper.operator.service;



import com.baomidou.mybatisplus.extension.service.IService;
import com.restkeeper.operator.entity.SysDictionary;

import java.util.List;

public interface ISysDictService extends IService<SysDictionary> {
    //分类获取字典表信息
    List<SysDictionary> getDictionaryList(String category);
}
