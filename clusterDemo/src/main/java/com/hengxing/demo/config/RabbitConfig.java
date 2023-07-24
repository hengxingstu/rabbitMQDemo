package com.hengxing.demo.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author hengxing
 * @version 1.0
 * @project RabbitMQ_demo
 * @date 7/23/2023 21:12:09
 */
@Configuration
public class RabbitConfig {
    public static final String QUEUE_NAME = "common_queue";

    @Bean
    Queue queue(){
        return new Queue(QUEUE_NAME,true,false,false);
    }
}
