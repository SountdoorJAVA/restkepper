package com.restkeeper.vo.store;

import lombok.Data;

import java.util.List;

@Data
public class PrivilegeVO {
    private List<PieVo> dataList;
    private Integer total;
}
