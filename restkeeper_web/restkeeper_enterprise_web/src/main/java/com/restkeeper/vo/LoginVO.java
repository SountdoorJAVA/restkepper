package com.restkeeper.vo;

import lombok.Data;

@Data
public class LoginVO{
    private int type;
    private String shopId;
    private String phone;
    private String password;
}

