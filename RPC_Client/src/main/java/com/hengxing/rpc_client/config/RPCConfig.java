package com.hengxing.rpc_client.config;


import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author hengxing
 * @version 1.0
 * @project RabbitMQ_demo
 * @date 7/11/2023 22:13:56
 */
@Configuration
public class RPCConfig {
    public static final String MESSAGE_QUEUE = "message_queue";
    public static final String REPLY_QUEUE = "reply_queue";
    public static final String RPC_EXCHANGE = "rpc_exchange";

    @Bean
    Queue messageQueue(){
        return new Queue(MESSAGE_QUEUE,true,false,false);
    }

    @Bean
    Queue replyQueue(){
        return new Queue(REPLY_QUEUE,true,false,false);
    }

    @Bean
    DirectExchange RPCExchange(){
        return new DirectExchange(RPC_EXCHANGE,true,false);
    }

    //请求队列和交换器绑定
    @Bean
    Binding RPCBinding(){
        return BindingBuilder.bind(messageQueue())
                .to(RPCExchange())
                .with(MESSAGE_QUEUE);
    }

    //返回队列和交换器绑定
    @Bean
    Binding replyBinding(){
        return BindingBuilder.bind(replyQueue())
                .to(RPCExchange())
                .with(REPLY_QUEUE);
    }

    /**
     * 由rabbitTemplate发送和接收消息
     * 设置回调队列地址
     */
    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setReplyAddress(REPLY_QUEUE);
        template.setReplyTimeout(6000);
        return template;
    }

    @Bean
    SimpleMessageListenerContainer replyContainer(ConnectionFactory connectionFactory){
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(REPLY_QUEUE);
        container.setMessageListener(rabbitTemplate(connectionFactory));
        return container;
    }
}
