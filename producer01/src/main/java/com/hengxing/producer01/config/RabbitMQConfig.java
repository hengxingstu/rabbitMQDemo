package com.hengxing.producer01.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author hengxing
 * @version 1.0
 * @project RabbitMQ_demo
 * @date 7/8/2023 17:16:00
 */
@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_NAME = "hengxing";

    @Bean
    Queue MyQueue(){
        /**
         * Construct a new queue, given a name, durability, exclusive and auto-delete flags.
         * Params:
         * name – the name of the queue.
         * durable – rabbitmq重启之后，队列是否还存在
         * exclusive – true：只能是创建这个队列的连接才能给这个队列发消息
         * autoDelete – true if the server should delete the queue when it is no longer in use
         */
        return new Queue(QUEUE_NAME,true,false,false);
    }
}
