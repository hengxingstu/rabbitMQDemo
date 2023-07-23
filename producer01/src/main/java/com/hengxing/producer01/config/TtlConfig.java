package com.hengxing.producer01.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author hengxing
 * @version 1.0
 * @project RabbitMQ_demo
 * @date 7/15/2023 15:45:22
 */
@Configuration
public class TtlConfig {
    public static final String TTL_EXCHANGE_NAME = "ttl_exchange";
    public static final String TTL_QUEUE_01 = "ttl_queue_01";
    public static final String TTL_QUEUE_02 = "ttl_queue_02";

    @Bean
    DirectExchange TTLDirectExchange(){
        return new DirectExchange(TTL_EXCHANGE_NAME, true, false);
    }

    @Bean
    Queue TtlQueue01(){
        return new Queue(TTL_QUEUE_01,true,false,false);
    }

    @Bean
    Queue TtlQueue02(){
        Map<String, Object> arguments = new HashMap<>();
        int second = 1000;
        arguments.put("x-message-ttl",10000);
        arguments.put("x-dead-letter-exchange",DlxConfig.DLX_EXCHANGE_NAME);//发到死信交换机
        arguments.put("x-dead-letter-routing-key",DlxConfig.DLX_QUEUE_01);//发到哪个死信队列？
        return new Queue(TTL_QUEUE_02,true,false,false,arguments);
    }

    @Bean
    Binding TtlBinding01(){
        return BindingBuilder.bind(TtlQueue01())
                .to(TTLDirectExchange())
                .with(TTL_QUEUE_01);
    }

    @Bean
    Binding TtlBinding02(){
        return BindingBuilder.bind(TtlQueue02())
                .to(TTLDirectExchange())
                .with(TTL_QUEUE_02);
    }
}
