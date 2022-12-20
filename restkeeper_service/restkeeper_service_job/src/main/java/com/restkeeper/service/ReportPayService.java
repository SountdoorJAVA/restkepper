package com.restkeeper.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.restkeeper.entity.ReportPay;

public interface ReportPayService extends IService<ReportPay> {

    void generateData();

}
