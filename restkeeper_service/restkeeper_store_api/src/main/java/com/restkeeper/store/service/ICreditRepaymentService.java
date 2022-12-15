package com.restkeeper.store.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.restkeeper.store.entity.CreditRepayment;

public interface ICreditRepaymentService extends IService<CreditRepayment> {
    boolean repayment(CreditRepayment creditRepayment);
}
