package com.hengxing.consumer02.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.HeadersExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author hengxing
 * @version 1.0
 * @project RabbitMQ_demo
 * @date 7/11/2023 20:35:59
 */
@Configuration
public class HeaderConfig {

    public static final String HEADER_EXCHANGE_NAME = "header_exchange01";
    public static final String HEADER_QUEUE_01 = "header_queue_01";
    public static final String HEADER_QUEUE_02 = "header_queue_02";

    @Bean
    Queue headerQueue01(){
        return new Queue(HEADER_QUEUE_01,true,false,false);
    }

    @Bean
    Queue headerQueue02(){
        return new Queue(HEADER_QUEUE_02,true,false,false);
    }

    @Bean
    HeadersExchange headersExchange(){
        return new HeadersExchange(HEADER_EXCHANGE_NAME,true,false);
    }

    @Bean
    Binding headerBinding01(){
        return BindingBuilder.bind(headerQueue01())
                .to(headersExchange())
                //头部信息中有name属性的消息，会被接收
                .where("name").exists();
    }

    @Bean
    Binding headerBinding02(){
        return BindingBuilder.bind(headerQueue02())
                .to(headersExchange())
                //头部信息中有age属性，并且值为99 的消息，会被接收
                .where("age").matches(99);
    }
}
