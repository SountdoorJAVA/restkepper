package com.restkeeper.sms;

import lombok.Data;

import java.io.Serializable;

@Data
public class SmsObject implements Serializable {
    //网络传输对象必须序列化
    private static final long serialVersionUID = -6986749569115643762L;

    private String phoneNumber;

    private String signName;

    private String templateCode;

    private String templateJsonParam;
}
