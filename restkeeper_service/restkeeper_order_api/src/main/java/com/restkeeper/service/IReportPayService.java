package com.restkeeper.service;

import com.restkeeper.dto.DayAmountCollectDTO;
import com.restkeeper.dto.PayTypeCollectDTO;
import com.restkeeper.dto.PrivilegeDTO;

import java.time.LocalDate;
import java.util.List;

public interface IReportPayService {
    /**
     * 获取一段日期内的销售趋势（应收概况）
     * @return
     */
    List<DayAmountCollectDTO> getDayAmountCollect(LocalDate start, LocalDate end);

    /**
     * 获取一定日期内的收款构成
     * @param start
     * @param end
     * @return
     */
    List<PayTypeCollectDTO> getPayTypeCollect(LocalDate start, LocalDate end);

    /**
     * 获取一定日期内的优惠统计
     * @param start
     * @param end
     * @return
     */
    PrivilegeDTO getPrivilegeCollectByDate(LocalDate start, LocalDate end);
}
