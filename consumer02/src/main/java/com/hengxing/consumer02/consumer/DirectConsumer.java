package com.hengxing.consumer02.consumer;

import com.hengxing.consumer02.config.ExchangeConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author hengxing
 * @version 1.0
 * @project RabbitMQ_demo
 * @date 7/10/2023 06:29:59
 */
@Component
public class DirectConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DirectConsumer.class);

    @RabbitListener(queues = ExchangeConfig.QUEUE_NAME_01)
    public void handleMessage(String message){
        LOGGER.info(ExchangeConfig.QUEUE_NAME_01 + ":" + message);
    }

    @RabbitListener(queues = ExchangeConfig.QUEUE_NAME_02)
    public void handleMessage02(String message){
        LOGGER.info(ExchangeConfig.QUEUE_NAME_02 + ":" + message);
    }
}
