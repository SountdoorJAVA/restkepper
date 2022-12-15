package com.restkeeper.vo.store;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@ContentRowHeight(15)//内容单元格行高
@HeadRowHeight(20)//头部单元格行高
@ColumnWidth(25)//字段宽度
public class CreditLogExcelVO {

    @ColumnWidth(50)
    @ExcelProperty(value = "订单号",index = 0)
    private String orderId;

    @ExcelProperty(value = "挂账人",index = 1)
    private String userName;

    @ExcelProperty(value = "挂账类型",index = 2)
    private String creditType;

    @ExcelProperty(value = "订单金额",index = 3)
    private double orderAmount;

    @ExcelProperty(value = "应收金额",index = 4)
    private double revenueAmount;

    @ExcelProperty(value = "挂账金额",index = 5)
    private double creditAmount;

    @DateTimeFormat(pattern = "yyyy年MM月dd日HH时mm分ss秒")
    @ExcelProperty(value = "挂账时间",index = 6)
    private Date dateTime;
}

