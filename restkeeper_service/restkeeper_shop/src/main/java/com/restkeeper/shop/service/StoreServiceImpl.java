package com.restkeeper.shop.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.restkeeper.constants.SystemCode;
import com.restkeeper.shop.dto.StoreDTO;
import com.restkeeper.shop.entity.Store;
import com.restkeeper.shop.mapper.StoreMapper;
import com.restkeeper.utils.BeanListUtils;
import com.restkeeper.utils.JWTUtil;
import com.restkeeper.utils.Result;
import com.restkeeper.utils.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service(version = "1.0.0", protocol = "dubbo")
@org.springframework.stereotype.Service("storeService")
@Slf4j
public class StoreServiceImpl extends ServiceImpl<StoreMapper, Store> implements IStoreService {
    @Override
    public IPage<Store> queryPageByName(int pageNo, int pageSize, String name) {
        //分页查询
        IPage<Store> page = new Page<>(pageNo, pageSize);
        //条件包装
        QueryWrapper<Store> qw = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(name)) {
            qw.lambda().like(Store::getStoreName, name);
        }
        return this.page(page, qw);
    }

    @Override
    public List<String> getAllProvince() {
        return getBaseMapper().getAllProvince();
    }

    @Override
    public List<StoreDTO> getStoreByProvince(String province) {
        //通过省份查询该省份下营业的门店
        QueryWrapper<Store> queryWrapper = new QueryWrapper<Store>();
        queryWrapper.lambda().eq(Store::getStatus, 1);
        if (!StringUtils.isEmpty(province) && !"all".equalsIgnoreCase(province)) {
            queryWrapper.lambda().eq(Store::getProvince, province);
        }
        List<Store> list = this.list(queryWrapper);

        List<StoreDTO> list_dto;
        try {
            return list_dto = BeanListUtils.copy(list, StoreDTO.class);
        } catch (Exception e) {
            log.info("转换出错");
        }
        return new ArrayList<StoreDTO>();
    }

    @Override
    public List<StoreDTO> getStoresByManagerId() {
        QueryWrapper<Store> queryWrapper = new QueryWrapper<Store>();
        queryWrapper.lambda()
                .eq(Store::getStatus, 1)
                .eq(Store::getStoreManagerId, RpcContext.getContext().getAttachment("loginUserId"));
        List<Store> list = this.list(queryWrapper);
        try {
            return BeanListUtils.copy(list, StoreDTO.class);
        } catch (Exception e) {
            log.info("转换出错");
        }
        return new ArrayList<StoreDTO>();
    }

    @Value("${gateway.secret}")
    private String secret;

    @Override
    public Result switchStore(String storeId) {
        Result result = new Result();
        Map<String, Object> tokenMap = Maps.newHashMap();
        tokenMap.put("shopId", RpcContext.getContext().getAttachment("shopId"));
        tokenMap.put("storeId", storeId);
        tokenMap.put("loginUserId", RpcContext.getContext().getAttachment("loginUserId"));
        tokenMap.put("loginUserName", RpcContext.getContext().getAttachment("loginUserName"));
        tokenMap.put("userType", SystemCode.USER_TYPE_STORE_MANAGER);
        String tokenInfo = "";
        try {
            tokenInfo = JWTUtil.createJWTByObj(tokenMap, secret);
        } catch (IOException e) {
            e.printStackTrace();
            result.setStatus(ResultCode.error);
            result.setDesc("生成令牌失败");
            return result;
        }
        result.setStatus(ResultCode.success);
        result.setDesc("ok");
        result.setData(storeId);
        result.setToken(tokenInfo);
        return result;
    }
}
