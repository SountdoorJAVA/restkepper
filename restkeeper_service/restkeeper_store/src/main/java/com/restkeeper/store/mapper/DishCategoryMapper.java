package com.restkeeper.store.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.restkeeper.redis.MybatisRedisCache;
import com.restkeeper.store.entity.DishCategory;
import org.apache.ibatis.annotations.CacheNamespace;

/**
 * <p>
 * 菜品及套餐分类 Mapper 接口
 * </p>
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface DishCategoryMapper extends BaseMapper<DishCategory> {

}
