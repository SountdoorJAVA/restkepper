package com.restkeeper.service;

import com.restkeeper.entity.DishEs;
import com.restkeeper.entity.SearchResult;

public interface IDishSearchService {
    //根据商品码和类型查询
    SearchResult<DishEs> searchAllByCode(String code, int type, int pageNum, int pageSize);
    //根据商品码查询
    SearchResult<DishEs> searchDishByCode(String code, int pageNum, int pageSize);
    //根据商品名查询
    SearchResult<DishEs> searchDishByName(String name, int type, int pageNum, int pageSize);
}
