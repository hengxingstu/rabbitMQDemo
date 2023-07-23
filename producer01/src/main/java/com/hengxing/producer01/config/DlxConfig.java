package com.hengxing.producer01.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author hengxing
 * @version 1.0
 * @project RabbitMQ_demo
 * @date 7/15/2023 17:02:24
 */
@Configuration
public class DlxConfig {

    public static final String DLX_EXCHANGE_NAME = "dlx_exchange_name";
    public static final String DLX_QUEUE_01 = "dlx_queue_01";
    public static final String DLX_QUEUE_02 = "DLX_QUEUE_02";

    @Bean
    DirectExchange DLXExchange(){
        return new DirectExchange(DLX_EXCHANGE_NAME,true,false);
    }

    @Bean
    Queue dlxQueue(){
        return new Queue(DLX_QUEUE_01,true,false,false);
    }

    @Bean
    Binding binding(){
        return BindingBuilder.bind(dlxQueue())
                .to(DLXExchange())
                .with(DLX_QUEUE_01);
    }
}
