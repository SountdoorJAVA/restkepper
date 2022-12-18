package com.restkeeper.vo.store;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

/**
 * 柱状图
 */
@Data
public class BarChartCollectVO{
    private List<String> xAxis = Lists.newArrayList();
    private List<Integer> series = Lists.newArrayList();
}
