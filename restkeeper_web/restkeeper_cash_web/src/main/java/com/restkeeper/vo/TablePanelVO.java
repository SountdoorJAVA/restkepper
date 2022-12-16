package com.restkeeper.vo;

import com.restkeeper.response.vo.PageVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 收银端桌台面板
 */
@Data
public class TablePanelVO {
    @ApiModelProperty(value = "空闲桌台")
    private Integer freeNumbers; //table状态0

    @ApiModelProperty(value = "已开台")
    private int openedNumbers; //table状态2

    @ApiModelProperty(value = "锁定")
    private int lockedNumbers; //table状态1

    @ApiModelProperty(value = "桌台分页信息")
    PageVO<TableVO> tablePage;
}

