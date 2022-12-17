package com.restkeeper.utils;

import com.restkeeper.SpringUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class SequenceUtils {

    static final int DEFAULT_LENGTH = 4;

    /**
     * 自定义流水
     *
     * @param prefix date + storeId
     * @return
     */
    public static String getSequenceWithPrefix(String prefix) {
        Long seq = getIncrementNum(prefix);
        String str = String.valueOf(seq);
        int len = str.length();
        if (len >= DEFAULT_LENGTH) {// 取决于业务规模,应该不会到达4
            return str;
        }
        int rest = DEFAULT_LENGTH - len;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rest; i++) {
            sb.append('0');
        }
        sb.append(str);
        return prefix + sb.toString();
    }

    /**
     * @param key 多租户下key =storeId
     * @return
     */
    public static String getSequence(String key) {
        //拿到当前日期的string
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMdd");
        String date = now.format(format);
        //每个门店每天一个新的流水号
        //拿到当前最新的流水号
        Long seq = getIncrementNum(date + key);
        String str = String.valueOf(seq);
        int len = str.length();//当前流水号长度
        if (len >= DEFAULT_LENGTH) {// 取决于业务规模,应该不会到达4
            return str;
        }
        //流水号长度设为4位,如果当前流水号小于4位,就在流水号前追加0
        int rest = DEFAULT_LENGTH - len;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rest; i++) {
            sb.append('0');
        }
        sb.append(str);
        //最终流水号为 8位年月日+4位流水号
        return date + sb.toString();
    }


    private static Long getIncrementNum(String key) {
        RedisTemplate<String, Object> redisTemplate = (RedisTemplate<String, Object>) SpringUtil.getBeanByName("redisTemplate");
        //该方法进行redis查询,如果redis中存在key的键值对,那么,则取其值;否则，将key对应的key值设置为0
        RedisAtomicLong entityIdCounter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
        //对得到的key值进行原子增并获得
        Long counter = entityIdCounter.incrementAndGet();
        //如果key值为空或者等于1,就是设置1天过期
        if ((null == counter || counter.longValue() == 1)) {// 初始设置过期时间
            System.out.println("设置过期时间为1天!");
            entityIdCounter.expire(1, TimeUnit.DAYS);// 单位天
        }
        //如果当前key值大于1,就直接返回
        return counter;
    }
}