package com.hengxing.consumer02.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author hengxing
 * @version 1.0
 * @project RabbitMQ_demo
 * @date 7/10/2023 11:26:49
 */
@Configuration
public class TopicConfig {

    public static final String TOPIC_EXCHANGE_NAME = "Topic_Exchange";
    public static final String HUAWEI_NEWS_QUEUE01 = "huawei_news_queue01";
    public static final String XIAOMI_NEWS_QUEUE01 = "xiaomi_news_queue01";
    public static final String XIAOMI_PHONE_NEWS_QUEUE01 = "xiaomi_phone_news_queue01";

    @Bean
    Queue huaweiQueue(){
        return new Queue(HUAWEI_NEWS_QUEUE01,true,false,false);
    }

    @Bean
    Queue xiaomiQueue(){
        return new Queue(XIAOMI_NEWS_QUEUE01,true,false,false);
    }

    @Bean
    Queue xiaomiPhoneQueue(){
        return new Queue(XIAOMI_PHONE_NEWS_QUEUE01,true,false,false);
    }

    @Bean
    TopicExchange topicExchange(){
        return new TopicExchange(TOPIC_EXCHANGE_NAME,true,false);
    }

    @Bean
    Binding xiaomiBinding(){
        return BindingBuilder.bind(xiaomiQueue())
                .to(topicExchange())
                .with("xiaomi.#");
        //# 表示通配符，如果routingKey是xiaomi.xxx，那么这个消息就会被转换到xiaomiQueue队列中
    }

    @Bean
    Binding huaweiBinding(){
        return BindingBuilder.bind(huaweiQueue())
                .to(topicExchange())
                .with("huawei.#");
    }

    @Bean
    Binding phoneBinding(){
        return BindingBuilder.bind(xiaomiPhoneQueue())
                .to(topicExchange())
                .with("#.phone.#");
    }

}
