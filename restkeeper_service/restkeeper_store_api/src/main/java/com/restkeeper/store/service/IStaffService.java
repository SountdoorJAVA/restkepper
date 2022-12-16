package com.restkeeper.store.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.restkeeper.store.entity.Staff;
import com.restkeeper.utils.Result;

public interface IStaffService extends IService<Staff> {
    //添加员工
    boolean addStaff(Staff staff);
    Result login(String shopId, String loginName, String loginPass);
}
