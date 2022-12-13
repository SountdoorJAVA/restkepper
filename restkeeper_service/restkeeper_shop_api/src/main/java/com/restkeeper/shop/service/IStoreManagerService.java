package com.restkeeper.shop.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.restkeeper.shop.entity.StoreManager;
import com.restkeeper.utils.Result;


import java.util.List;

public interface IStoreManagerService extends IService<StoreManager> {
    //分页查询门店管理员
    IPage<StoreManager> queryPageByCriteria(int pageNo, int pageSize, String criteria);

    //门店管理员添加逻辑
    boolean addStoreManager(String name, String phone, List<String> storeIds);

    //门店管理员修改
    boolean updateStoreManager(String storeManagerId, String name, String phone, List<String> storeIds);

    //停用
    boolean pauseStoreManager(String id);

    //逻辑删除
    boolean deleteStoreManager(String id);

    //门店管理员登录接口
    Result login(String shopId, String phone, String loginPass);
}
