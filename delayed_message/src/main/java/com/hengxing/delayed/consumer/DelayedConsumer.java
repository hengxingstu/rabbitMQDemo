package com.hengxing.delayed.consumer;

import com.hengxing.delayed.config.DelayedConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author hengxing
 * @version 1.0
 * @project RabbitMQ_demo
 * @date 7/15/2023 20:06:57
 */
@Component
public class DelayedConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DelayedConsumer.class);

    @RabbitListener(queues = DelayedConfig.DELAYED_QUEUE_NAME)
    public void handelDelayed(String message){
        LOGGER.info("message from Delayed:" + message + "-->收到信息的时间" + new Date());
    }
}
