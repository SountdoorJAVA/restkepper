package com.restkeeper.operator.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.restkeeper.constants.SystemCode;
import com.restkeeper.operator.config.RabbitMQConfig;
import com.restkeeper.operator.entity.EnterpriseAccount;
import com.restkeeper.operator.mapper.EnterpriseAccountMapper;
import com.restkeeper.sms.SmsObject;
import com.restkeeper.utils.*;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;

/**
 * @author MORRIS --> Java
 * @date 2022-12-11 16:25:10
 */
@Service(version = "1.0.0", protocol = "dubbo")
@RefreshScope
public class EnterpriseAccountServiceImpl extends ServiceImpl<EnterpriseAccountMapper, EnterpriseAccount> implements IEnterpriseAccountService {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${gateway.secret}")
    private String secret;

    @Value("${sms.operator.signName}")
    private String signName;

    @Value("${sms.operator.templateCode}")
    private String templateCode;

    //当企业用户创建成功或者修改密码成功后,发送短信到消息队列
    private void sendMessage(String phone, String shopId, String pwd) {
        SmsObject smsObject = new SmsObject();
        smsObject.setPhoneNumber(phone);//设置手机号
        smsObject.setSignName(signName);//设置签名
        smsObject.setTemplateCode(templateCode);//设置模板编号
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("shopId", shopId);
        jsonObject.put("password", pwd);
        smsObject.setTemplateJsonParam(jsonObject.toJSONString());//设置模板参数
        rabbitTemplate.convertAndSend(RabbitMQConfig.ACCOUNT_QUEUE, JSON.toJSONString(smsObject));//发送到rabbitmq的队列
    }

    @Override
    public IPage<EnterpriseAccount> queryPageByName(int pageNum, int pageSize, String enterpriseName) {

        IPage<EnterpriseAccount> page = new Page<>(pageNum, pageSize);
        QueryWrapper<EnterpriseAccount> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(enterpriseName)) {
            queryWrapper.like("enterprise_name", enterpriseName);
        }
        return this.page(page, queryWrapper);

    }

    @Override
    @Transactional //事务
    public boolean add(EnterpriseAccount account) {
        boolean flag = true;
        try {
            //账号,密码特殊处理
            String shopId = getShopId();
            account.setShopId(shopId);
            //密码随机6位
            String pwd = RandomStringUtils.randomNumeric(6);
            System.out.println("企业账号 秘密");
            System.out.println(account.getEnterpriseName() + " " + pwd);
            account.setPassword(Md5Crypt.md5Crypt(pwd.getBytes()));
            this.save(account);
            //发送短信
            sendMessage(account.getPhone(), shopId, pwd);
        } catch (Exception ex) {
            flag = false;
            throw ex;
        }
        return flag;
    }

    @Override
    @Transactional //事务
    public boolean recovery(String id) {
        return this.getBaseMapper().recovery(id);
    }

    @Override
    @Transactional //事务
    public boolean resetPwd(String id, String password) {
        boolean flag = true;
        try {
            //查询用户信息
            EnterpriseAccount account = this.getById(id);
            if (account == null) {
                return false;
            }
            String newPwd;
            //如果设置了要重置密码
            if (StringUtils.isNotEmpty(password)) {
                newPwd = password;
            } else {
                //如果没有设置要重置密码
                newPwd = RandomStringUtils.randomNumeric(6);
            }
            account.setPassword(Md5Crypt.md5Crypt(newPwd.getBytes()));
            this.updateById(account);
            //发送短信
            sendMessage(account.getPhone(), account.getShopId(), newPwd);
        } catch (Exception ex) {
            flag = false;
            throw ex;
        }
        return flag;
    }

    //登录
    @Override
    public Result login(String shopId, String phone, String loginPass) {
        Result result = new Result();
        //参数校验
        if (StringUtils.isEmpty(shopId)) {
            result.setStatus(ResultCode.error);
            result.setDesc("商户号未输入");
            return result;
        }
        if (StringUtils.isEmpty(phone)) {
            result.setStatus(ResultCode.error);
            result.setDesc("手机号未输入");
            return result;
        }
        if (StringUtils.isEmpty(loginPass)) {
            result.setStatus(ResultCode.error);
            result.setDesc("密码未输入");
            return result;
        }
        //查询用户信息
        QueryWrapper<EnterpriseAccount> qw = new QueryWrapper<>();
        //qw.eq("phone", phone); //硬编码不推荐
        qw.lambda().eq(EnterpriseAccount::getPhone, phone)
                .eq(EnterpriseAccount::getShopId, shopId)
                .notIn(EnterpriseAccount::getStatus, AccountStatus.Forbidden.getStatus());
        EnterpriseAccount enterpriseAccount = this.getOne(qw);
        if (enterpriseAccount == null) {
            result.setStatus(ResultCode.error);
            result.setDesc("账号不存在");
            return result;
        }
        //判断密码是否正确
        String salts = MD5CryptUtil.getSalts(enterpriseAccount.getPassword());
        if (!Md5Crypt.md5Crypt(loginPass.getBytes(), salts).equals(enterpriseAccount.getPassword())) {
            result.setStatus(ResultCode.error);
            result.setDesc("密码错误");
        }
        //生成jwt令牌
        HashMap<String, Object> tokenInfo = Maps.newHashMap();
        tokenInfo.put("shopId", enterpriseAccount.getShopId());//商户id
        tokenInfo.put("loginName", enterpriseAccount.getEnterpriseName());//用户名
        tokenInfo.put("loginType", SystemCode.USER_TYPE_SHOP);//用户类型
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
        result.setData(enterpriseAccount);
        result.setToken(token);
        return result;
    }

    //获取shopId,随机8位数字
    private String getShopId() {
        String shopId = RandomStringUtils.randomNumeric(8);
        //店铺校验,避免shopId重复
        QueryWrapper<EnterpriseAccount> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("shop_id", shopId);
        EnterpriseAccount enterpriseAccount = this.getOne(queryWrapper);
        if (enterpriseAccount != null) {
            this.getShopId();
        }
        return shopId;
    }
}
