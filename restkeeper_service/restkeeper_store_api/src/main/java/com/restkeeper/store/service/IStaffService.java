package com.restkeeper.store.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.restkeeper.store.entity.Staff;

public interface IStaffService extends IService<Staff> {
    //添加员工
    boolean addStaff(Staff staff);
}
