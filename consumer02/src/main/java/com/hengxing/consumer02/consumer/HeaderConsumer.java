package com.hengxing.consumer02.consumer;

import com.hengxing.consumer02.config.HeaderConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author hengxing
 * @version 1.0
 * @project RabbitMQ_demo
 * @date 7/11/2023 20:44:07
 */
@Component
public class HeaderConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(HeaderConsumer.class);

    @RabbitListener(queues = HeaderConfig.HEADER_QUEUE_01)
    public void handleMessage01(byte[] message){
        LOGGER.info(HeaderConfig.HEADER_QUEUE_01 + ": " + new String(message));
    }

    @RabbitListener(queues = HeaderConfig.HEADER_QUEUE_02)
    public void handleMessage02(byte[] message){
        LOGGER.info(HeaderConfig.HEADER_QUEUE_02 + ": " + new String(message));
    }
}
