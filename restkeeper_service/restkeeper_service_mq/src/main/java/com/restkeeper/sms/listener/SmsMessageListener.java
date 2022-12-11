package com.restkeeper.sms.listener;

import com.alibaba.alicloud.sms.ISmsService;
import com.alibaba.fastjson.JSON;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.restkeeper.constants.SystemCode;
import com.restkeeper.sms.SmsObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author MORRIS --> Java
 * @date 2022-12-11 22:36:32
 */
@Component
@Slf4j
public class SmsMessageListener {

    //@Autowired
    //private ISmsService smsService;

    @RabbitListener(queues = SystemCode.SMS_ACCOUNT_QUEUE)
    public void getAccountMessage(String message) {
        log.info("发送短信监听类接收到消息：" + message);
        SmsObject smsObject = JSON.parseObject(message, SmsObject.class);

        //发送短信
        //SendSmsResponse sendSmsResponse = this.sendSms(smsObject.getPhoneNumber(), smsObject.getSignName(), smsObject.getTemplateCode(), smsObject.getTemplateJsonParam());
        //log.info(JSON.toJSONString(sendSmsResponse));
    }

    //发送短信
    //private SendSmsResponse sendSms(String phoneNumber, String signName, String templateCode, String templateJsonParam) {
    //    // 组装请求对象-具体描述见控制台-文档部分内容
    //    SendSmsRequest request = new SendSmsRequest();
    //    // 必填:待发送手机号
    //    request.setPhoneNumbers(phoneNumber);
    //    // 必填:短信签名-可在短信控制台中找到
    //    request.setSignName(signName);
    //    // 必填:短信模板-可在短信控制台中找到
    //    request.setTemplateCode(templateCode);
    //    // 可选:模板中的变量替换JSON串,如模板内容为"【企业级分布式应用服务】,您的验证码为${code}"时,此处的值为
    //    request.setTemplateParam(templateJsonParam);
    //    SendSmsResponse sendSmsResponse;
    //    try {
    //        sendSmsResponse = smsService.sendSmsRequest(request);
    //    } catch (com.aliyuncs.exceptions.ClientException e) {
    //        e.printStackTrace();
    //        sendSmsResponse = new SendSmsResponse();
    //    }
    //    return sendSmsResponse;
    //}
}
