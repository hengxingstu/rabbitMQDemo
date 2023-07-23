package com.hengxing.consumer02.config;


import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author hengxing
 * @version 1.0
 * @project RabbitMQ_demo
 * @date 7/10/2023 06:17:19
 */
@Configuration
public class ExchangeConfig {
    /*队列和交换机名称*/
    public static final String QUEUE_NAME_01 = "hengxing01";
    public static final String QUEUE_NAME_02 = "hengxing02";
    public static final String EXCHANGE_NAME = "MyExchange";

    /* 创建队列*/
    @Bean
    Queue queue01(){
        return new Queue(QUEUE_NAME_01,true,false,false);
    }

    @Bean
    Queue queue02(){
        return new Queue(QUEUE_NAME_02,true,false,false);
    }

    /* 创建交换机*/
    @Bean
    DirectExchange MyExchange(){
        return new DirectExchange(EXCHANGE_NAME,true,false);
    }

    /* 绑定对应交换机和队列*/
    @Bean
    Binding binding01(){
        return BindingBuilder.bind(queue01())
                .to(MyExchange())
                .with(QUEUE_NAME_01);
    }

    @Bean
    Binding binding02(){
        return BindingBuilder.bind(queue02())
                .to(MyExchange())
                .with(QUEUE_NAME_02);
    }

}
