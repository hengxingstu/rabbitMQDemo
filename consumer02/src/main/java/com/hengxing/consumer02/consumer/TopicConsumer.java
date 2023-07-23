package com.hengxing.consumer02.consumer;

import com.hengxing.consumer02.config.TopicConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author hengxing
 * @version 1.0
 * @project RabbitMQ_demo
 * @date 7/10/2023 21:42:15
 */
@Component
public class TopicConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(TopicConsumer.class);

    @RabbitListener(queues = TopicConfig.HUAWEI_NEWS_QUEUE01)
    public void handleMessage01(String message){
        LOGGER.info(TopicConfig.HUAWEI_NEWS_QUEUE01 + ":" + message);
    }

    @RabbitListener(queues = TopicConfig.XIAOMI_NEWS_QUEUE01)
    public void handleMessage02(String message){
        LOGGER.info(TopicConfig.XIAOMI_NEWS_QUEUE01 + ":" + message);
    }

    @RabbitListener(queues = TopicConfig.XIAOMI_PHONE_NEWS_QUEUE01)
    public void handleMessage03(String message){
        LOGGER.info(TopicConfig.XIAOMI_PHONE_NEWS_QUEUE01 + ":" + message);
    }

}
