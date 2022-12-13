package com.restkeeper;

import org.springframework.context.ApplicationContext;

public class SpringUtil {
    private static ApplicationContext ac = null;

    public static ApplicationContext getApplicationContext() {
        return ac;
    }

    public static void setApplicationContext(ApplicationContext ac) {
        SpringUtil.ac = ac;
    }

    public static Object getBeanByName(String beanName) {
        return getApplicationContext().getBean(beanName);
    }


    public static Object getBeanByType(Class tClass) {
        return getApplicationContext().getBean(tClass);
    }

}
