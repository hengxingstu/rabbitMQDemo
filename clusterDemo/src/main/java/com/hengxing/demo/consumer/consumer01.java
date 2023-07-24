package com.hengxing.demo.consumer;

import com.hengxing.demo.config.RabbitConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author hengxing
 * @version 1.0
 * @project RabbitMQ_demo
 * @date 7/23/2023 21:30:59
 */
@Component
public class consumer01 {
    private static final Logger LOGGER = LoggerFactory.getLogger(consumer01.class);

    @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
    public void handler(String message){
        LOGGER.info("收到消息：" + message);
    }
}
