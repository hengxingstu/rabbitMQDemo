package com.hengxing.consumer02.consumer;

import com.hengxing.consumer02.config.FanOutConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author hengxing
 * @version 1.0
 * @project RabbitMQ_demo
 * @date 7/10/2023 10:52:36
 */
@Component
public class FanOutConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(FanOutConsumer.class);

    @RabbitListener(queues = FanOutConfig.QUEUE_NAME_01)
    public void consumer01(String message){
        LOGGER.info(FanOutConfig.QUEUE_NAME_01 + ":" + message);
    }

    @RabbitListener(queues = FanOutConfig.QUEUE_NAME_02)
    public void consumer02(String message){
        LOGGER.info(FanOutConfig.QUEUE_NAME_02 + ":" + message);
    }
}
