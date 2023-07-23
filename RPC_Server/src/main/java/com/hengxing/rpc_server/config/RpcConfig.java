package com.hengxing.rpc_server.config;

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
 * @date 7/13/2023 20:40:39
 */
@Configuration
public class RpcConfig {

    public static final String MESSAGE_QUEUE = "message_queue";
    public static final String REPLY_QUEUE = "reply_queue";
    public static final String RPC_EXCHANGE = "rpc_exchange";

    //消息发送队列
    @Bean
    Queue messageQueue(){
        return new Queue(MESSAGE_QUEUE,true,false,false);
    }

    //消息返回队列
    @Bean
    Queue replyQueue(){
        return new Queue(REPLY_QUEUE,true,false,false);
    }

    @Bean
    DirectExchange directExchange(){
        return new DirectExchange(RPC_EXCHANGE);
    }

    //绑定请求队列
    @Bean
    Binding messageBinding(){
        return BindingBuilder.bind(messageQueue()).to(directExchange()).with(MESSAGE_QUEUE);
    }

    //绑定返回队列
    @Bean
    Binding replyBinding(){
        return BindingBuilder.bind(replyQueue()).to(directExchange()).with(REPLY_QUEUE);
    }
}
