package com.restkeeper.store.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.restkeeper.store.entity.DishCategory;

import java.util.List;
import java.util.Map;

/**
 * @author MORRIS --> Java
 * @date 2022-12-14 16:45:05
 */
public interface IDishCategoryService extends IService<DishCategory> {
    boolean add(String name, int type);
    boolean update(String id, String categoryName);
    IPage<DishCategory> queryPage(int pageNum, int pageSize);
    List<Map<String,Object>> findCategoryList(Integer type);
}
