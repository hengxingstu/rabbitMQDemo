package com.hengxing.delayed.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 延迟队列设置
 * @author hengxing
 * @version 1.0
 * @project RabbitMQ_demo
 * @date 7/15/2023 19:45:33
 */
@Configuration
public class DelayedConfig {

    public static final String DELAYED_EXCHANGE_NAME = "delayed_exchange";
    public static final String DELAYED_EXCHANGE_TYPE = "x-delayed-message";//这个参数声明其是一个延迟队列
    public static final String DELAYED_QUEUE_NAME = "delayed_queue";

    @Bean
    Queue delayedQueue(){
        return new Queue(DELAYED_QUEUE_NAME,true,false,false);
    }

    @Bean
    CustomExchange delayedExchange(){
        Map<String, Object> argument = new HashMap<>();
        argument.put("x-delayed-type","direct");//声明这是一个直连交换机，四种交换机都可选
        return new CustomExchange(DELAYED_EXCHANGE_NAME,DELAYED_EXCHANGE_TYPE,true,false,argument);
    }

    @Bean
    Binding binding(){
        return BindingBuilder.bind(delayedQueue())
                .to(delayedExchange())
                .with(DELAYED_QUEUE_NAME)
                .noargs();//这个无参是CustomExchange这种交换机特殊需求
    }
}
