package com.restkeeper.shop.config;

import com.restkeeper.constants.SystemCode;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * rabbitMQ配置类
 */
@Component
public class RabbitMQConfig {

    //帐号下发队列
    public static final String ACCOUNT_QUEUE = SystemCode.SMS_ACCOUNT_QUEUE;

    //帐号下发routingkey
    public static final String ACCOUNT_QUEUE_KEY ="account_queue_key";

    //短信发送交换机
    public static final String SMS_EXCHANGE = "sms_exchange";

    //声明队列
    @Bean(ACCOUNT_QUEUE)
    public Queue accountQueue(){
        Queue queue = new Queue(ACCOUNT_QUEUE);
        return queue;
    }

    //声明交换机
    @Bean(SMS_EXCHANGE)
    public Exchange smsExchange(){
        return ExchangeBuilder.directExchange(SMS_EXCHANGE).build();
    }

    //队列绑定交换机
    @Bean
    public Binding accountQueueToSmsExchange(@Qualifier(ACCOUNT_QUEUE) Queue queue, @Qualifier(SMS_EXCHANGE) Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(ACCOUNT_QUEUE_KEY).noargs();
    }
}

