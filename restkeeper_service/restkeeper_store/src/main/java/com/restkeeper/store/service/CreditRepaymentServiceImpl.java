package com.restkeeper.store.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restkeeper.exception.BussinessException;
import com.restkeeper.store.entity.CreditRepayment;
import com.restkeeper.store.mapper.CreditRepaymentMapper;
import lombok.val;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

@org.springframework.stereotype.Service("creditRepaymentService")
@Service(version = "1.0.0", protocol = "dubbo")
public class CreditRepaymentServiceImpl extends ServiceImpl<CreditRepaymentMapper, CreditRepayment> implements ICreditRepaymentService {
    @Autowired
    @Qualifier("creditService")
    private ICreditService creditService;

    @Override
    @Transactional
    public boolean repayment(CreditRepayment creditRepayment) {
        try {
            //获取当前还款金额
            Integer repaymentAmount = creditRepayment.getRepaymentAmount();
            //获取挂账信息
            val credit = creditService.getById(creditRepayment.getCreditId());
            if (credit.getCreditAmount() - repaymentAmount < 0) {
                throw new BussinessException("还款金额超过欠款金额");
            }
            //新增还款记录
            this.save(creditRepayment);
            //修改欠款金额
            credit.setCreditAmount(credit.getCreditAmount() - repaymentAmount);
            //修改还款金额
            credit.setTotalRepaymentAmount(credit.getTotalRepaymentAmount() + repaymentAmount);
            //更新挂账信息
            return creditService.saveOrUpdate(credit);
        } catch (Exception e) {
            throw new BussinessException("还款失败");
        }
    }
}
