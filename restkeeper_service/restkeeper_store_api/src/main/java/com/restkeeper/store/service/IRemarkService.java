package com.restkeeper.store.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.restkeeper.store.entity.Remark;

import java.util.List;

public interface IRemarkService extends IService<Remark> {
    /**
     * 获取门店信息
     *
     * @return
     */
    List<Remark> getRemarks();

    /**
     * 添加备注
     *
     * @param remarks
     * @return
     */
    boolean updateRemarks(List<Remark> remarks);
}
