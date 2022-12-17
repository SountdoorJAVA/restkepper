package com.restkeeper.operator.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.restkeeper.operator.entity.OperatorUser;
import com.restkeeper.operator.mapper.OperatorUserMapper;
import com.restkeeper.shop.service.IStoreManagerService;
import com.restkeeper.utils.JWTUtil;
import com.restkeeper.utils.MD5CryptUtil;
import com.restkeeper.utils.Result;
import com.restkeeper.utils.ResultCode;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

//@Service("operatorUserService")
@Service(version = "1.0.0", protocol = "dubbo")
/**
 * dubbo中支持的协议
 * dubbo 默认
 * rmi
 * hessian
 * http
 * webservice
 * thrift
 * memcached
 * redis
 */
public class OperatorUserServiceImpl extends ServiceImpl<OperatorUserMapper, OperatorUser> implements IOperatorUserService {

    @Value("${gateway.secret}")
    private String secret;

    @Override
    public IPage<OperatorUser> queryPageByName(int pageNum, int pageSize, String name) {
        IPage<OperatorUser> page = new Page<>(pageNum, pageSize);
        QueryWrapper<OperatorUser> queryWrapper = null;
        if (StringUtils.isNotEmpty(name)) {
            queryWrapper = new QueryWrapper<>();
            queryWrapper.like("loginname", name);
        }
        return this.page(page, queryWrapper);
    }

    @Override
    public Result login(String loginName, String loginPass) {
        System.out.println("-------------OperatorUserService----------------");
        System.out.println("-----------------------------");
        System.out.println(Thread.currentThread().getName());


        System.out.println("loginName=" + loginName + " loginPass=" + loginPass);
        Result result = new Result();
        //参数校验
        if (StringUtils.isNotEmpty(loginName)) {
            result.setStatus(ResultCode.error);
            result.setDesc("用户名不能为空");
        }
        if (StringUtils.isNotEmpty(loginPass)) {
            result.setStatus(ResultCode.error);
            result.setDesc("密码不能为空");
        }
        //查询用户是否存在
        QueryWrapper<OperatorUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("loginname", loginName);
        OperatorUser operatorUser = this.getOne(queryWrapper);
        if (operatorUser == null) {
            result.setStatus(ResultCode.error);
            result.setDesc("用户不存在");
        }
        //密码校验
        String salts = MD5CryptUtil.getSalts(operatorUser.getLoginpass());
        if (!Md5Crypt.md5Crypt(loginPass.getBytes(), salts).equals(operatorUser.getLoginpass())) {
            result.setStatus(ResultCode.error);
            result.setDesc("密码错误");
        }
        //生成jwt令牌
        HashMap<String, Object> tokenInfo = Maps.newHashMap();
        tokenInfo.put("loginName", operatorUser.getLoginname());
        String token = null;
        try {
            token = JWTUtil.createJWTByObj(tokenInfo, secret);
        } catch (IOException e) {
            e.printStackTrace();
            result.setStatus(ResultCode.error);
            result.setDesc("令牌生成失败");
            return result;
        }
        //返回结果
        result.setStatus(ResultCode.success);
        result.setDesc("ok");
        result.setData(operatorUser);
        result.setToken(token);
        return result;
    }
}
