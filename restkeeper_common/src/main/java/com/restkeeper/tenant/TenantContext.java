package com.restkeeper.tenant;

import java.util.LinkedHashMap;
import java.util.Map;

public class TenantContext {

    static ThreadLocal<Map<String, Object>> threadLocal = new ThreadLocal<Map<String, Object>>() {
        @Override
        protected Map<String, Object> initialValue() {
            return new LinkedHashMap<String, Object>();
        }
    };

    public static void addAttachment(String key, Object value) {
        threadLocal.get().put(key, value);
    }

    public static void addAttachments(Map<String, Object> map) {
        threadLocal.get().putAll(map);
    }

    public static String getShopId() {
        return String.valueOf(threadLocal.get().get("shopId"));
    }

    public static Map<String, Object> getAttachments() {
        return threadLocal.get();
    }

    public static String getStorId() {
        return String.valueOf(threadLocal.get().get("storeId"));
    }

    public static String getUserType() {
        return String.valueOf(threadLocal.get().get("userType"));
    }

    public static void clear() {
        threadLocal.remove();
    }

    public static String getLoginUserId() {
        return String.valueOf(threadLocal.get().get("loginUserId"));
    }

    public static String getLoginUserName() {
        return String.valueOf(threadLocal.get().get("loginUserName"));
    }
}
