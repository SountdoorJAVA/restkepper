package com.restkeeper.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class CreditDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String creditId; //挂账id
    private Integer creditAmount; //挂账金额
    private String creditUserName; //挂账人
}

