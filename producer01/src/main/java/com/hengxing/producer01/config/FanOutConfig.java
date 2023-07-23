package com.hengxing.producer01.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author hengxing
 * @version 1.0
 * @project RabbitMQ_demo
 * @date 7/10/2023 10:41:45
 */
@Configuration
public class FanOutConfig {
    public static final String QUEUE_NAME_01 = "FanOuthengxing01";
    public static final String QUEUE_NAME_02 = "FanOuthengxing02";
    public static final String EXCHANGE_NAME = "FanOutExchange";

    @Bean
    FanoutExchange fanoutExchange(){
        return new FanoutExchange(EXCHANGE_NAME,true,false);
    }

    @Bean
    Queue fanQueue01(){
        return new Queue(QUEUE_NAME_01,true,false,false);
    }

    @Bean
    Queue fanQueue02(){
        return new Queue(QUEUE_NAME_02,true,false,false);
    }

    @Bean
    Binding FanOutBinding01(){
        return BindingBuilder.bind(fanQueue01())
                .to(fanoutExchange());
    }

    @Bean
    Binding FanOutBinding02(){
        return BindingBuilder.bind(fanQueue02())
                .to(fanoutExchange());
    }
}
