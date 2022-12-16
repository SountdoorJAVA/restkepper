package com.restkeeper.store.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.client.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restkeeper.constants.SystemCode;
import com.restkeeper.exception.BussinessException;
import com.restkeeper.sms.SmsObject;
import com.restkeeper.store.mapper.StaffMapper;
import com.restkeeper.store.entity.Staff;
import com.restkeeper.utils.JWTUtil;
import com.restkeeper.utils.MD5CryptUtil;
import com.restkeeper.utils.Result;
import com.restkeeper.utils.ResultCode;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service(version = "1.0.0", protocol = "dubbo")
public class StaffServiceImpl extends ServiceImpl<StaffMapper, Staff> implements IStaffService {
    @Override
    @Transactional
    public boolean addStaff(Staff staff) {
        String password = staff.getPassword();
        if (StringUtils.isEmpty(password)) {
            password = RandomStringUtils.randomNumeric(8);
        }
        staff.setPassword(Md5Crypt.md5Crypt(password.getBytes()));
        try {
            this.save(staff);
            //发送短信
            this.sendMessage(staff.getPhone(), staff.getShopId(), staff.getIdNumber());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //秘钥
    @Value("${gateway.secret}")
    private String secret;

    @Override
    public Result login(String shopId, String loginName, String loginPass) {
        Result result = new Result();
        //校验数据
        if (StringUtils.isNotEmpty(shopId)) {
            result.setStatus(ResultCode.error);
            result.setDesc("商户不能为空");
            return result;
        }
        if (StringUtils.isNotEmpty(loginName)) {
            result.setStatus(ResultCode.error);
            result.setDesc("账号不能为空");
            return result;
        }
        if (StringUtils.isNotEmpty(loginPass)) {
            result.setStatus(ResultCode.error);
            result.setDesc("密码不能为空");
            return result;
        }
        //查询员工
        //QueryWrapper<Staff> qw = new QueryWrapper<>();
        //qw.lambda().eq(Staff::getShopId, shopId).eq(Staff::getStaffName, loginName);
        //val staff = this.getOne(qw);
        //调用mapper中自定义的方法实现查询,该方法跳过sql拦截器
        val staff = this.getBaseMapper().login(shopId, loginName);

        if (staff == null) {
            result.setStatus(ResultCode.error);
            result.setDesc("员工信息不存在");
            return result;
        }
        //校验密码
        val password = staff.getPassword();
        val salts = MD5CryptUtil.getSalts(password);
        if (!Md5Crypt.md5Crypt(loginPass.getBytes(), salts).equals(password)) {
            result.setStatus(ResultCode.error);
            result.setDesc("密码输入错误");
            return result;
        }
        //生成令牌
        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("shopId", shopId);
        tokenMap.put("storeId", staff.getStoreId());
        tokenMap.put("staffId", staff.getStaffId());
        tokenMap.put("loginUserId", staff.getStaffId());
        tokenMap.put("loginUserName", loginName);
        tokenMap.put("userType", SystemCode.USER_TYPE_STAFF);
        String jwtInfo = "";
        try {
            jwtInfo = JWTUtil.createJWTByObj(tokenMap, secret);
        } catch (IOException e) {
            log.error("加密失败{}", e.getMessage());
            result.setStatus(ResultCode.error);
            result.setDesc("加密失败");
            return result;
        }

        result.setStatus(ResultCode.success);
        result.setToken(jwtInfo);
        result.setDesc("ok");
        result.setData(staff);
        return result;
    }

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${sms.operator.signName}")
    private String signName;

    @Value("${sms.operator.templateCode}")
    private String templateCode;

    private void sendMessage(String phone, String shopId, String pwd) {
        SmsObject smsObject = new SmsObject();
        smsObject.setPhoneNumber(phone);
        smsObject.setSignName(signName);
        smsObject.setSignName(templateCode);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("shopId", shopId);
        jsonObject.put("password", pwd);
        smsObject.setTemplateJsonParam(jsonObject.toJSONString());

        rabbitTemplate.convertAndSend(SystemCode.SMS_ACCOUNT_QUEUE, JSON.toJSONString(smsObject));
    }
}
