package com.restkeeper.response.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.util.List;

/**
 * 前端对象转换封装
 * @param <T>
 */
@Data
public class PageVO<T> {

    private  long counts; //总个数

    private  long pagesize; //每页个数

    private  long pages; //总页数

    private  long page; //当前页

    private List<T> items; //数据记录

    public PageVO(IPage page) {
        this.pagesize = page.getSize();
        this.counts = page.getTotal();
        this.page = page.getCurrent();
        this.pages = page.getPages();
        this.items = page.getRecords();
    }
    //支持自定义列表
    public PageVO(IPage page,List<T> items) {
        this.pagesize = page.getSize();
        this.counts = page.getTotal();
        this.page = page.getCurrent();
        this.pages = page.getPages();
        this.items = items;
    }

    public PageVO() {
    }
}
