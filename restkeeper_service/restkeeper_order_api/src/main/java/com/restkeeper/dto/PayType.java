package com.restkeeper.dto;

/**
 * @author MORRIS --> Java
 * @date 2022-12-20 11:26:15
 */
public enum PayType{
    Free(0,"免单"),
    Cash(1,"现金"),
    WeiXin(2,"微信"),
    AliPay(3,"支付宝"),
    Card(4,"银行卡");

    private String name;
    private int value;

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    private PayType(int value,String name){
        this.name = name;
        this.value = value;
    }

    /**
     * 根据值获取枚举名称
     * @param value
     * @return
     */
    public static String getName(int value){
        for (PayType p : PayType.values()){
            if(p.getValue() == value) return p.getName();
        }

        return null;
    }
}
