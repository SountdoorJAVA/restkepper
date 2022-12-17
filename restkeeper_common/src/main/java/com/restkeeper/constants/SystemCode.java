package com.restkeeper.constants;

/**
 * 全局常量类
 *
 * @author MORRIS --> Java
 * @date 2022-12-11 22:33:24
 */

public class SystemCode {
    // 口味描述
    public final static String DISH_FLAVOR = "flavor";

    //运营端账号下发队列
    public final static String SMS_ACCOUNT_QUEUE = "account_queue";
    //1 集团类型  2 门店类型
    public final static String USER_TYPE_SHOP = "1"; //集团用户类型
    public final static String USER_TYPE_STORE_MANAGER = "2"; //门店管理员类型
    public final static String USER_TYPE_STAFF = "3";  //普通员工

    //禁用
    public final static int FORBIDDEN = 0;
    //开启
    public final static int ENABLED = 1;

    public final static int DISH_TYPE_MORMAL = 1; //普通菜品
    public final static int DISH_TYPE_SETMEAL = 2; //套餐

    // 挂账类型：1 个人 2 公司
    public final static int CREDIT_TYPE_USER = 1;
    public final static int CREDIT_TYPE_COMPANY = 2;

    public final static int TABLE_STATUS_FREE = 0; // 0空闲
    public final static int TABLE_STATUS_LOCKED = 1; // 1 锁定
    public final static int TABLE_STATUS_OPEND = 2; // 2 已开桌

    // 订单状态 0：未付 1：已付
    public final static int ORDER_STATUS_NOTPAY = 0;
    public final static int ORDER_STATUS_PAYED = 1;
    // 订单来源 0 门店 1 app
    public final static int ORDER_SOURCE_STORE = 0;
    public final static int ORDER_SOURCE_APP = 1;
    public final static String DICTIONARY_REMARK = "remark"; //字典表备注类型
}
