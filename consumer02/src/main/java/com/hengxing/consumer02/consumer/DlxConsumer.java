package com.hengxing.consumer02.consumer;

import com.hengxing.consumer02.config.DlxConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author hengxing
 * @version 1.0
 * @project RabbitMQ_demo
 * @date 7/15/2023 17:25:57
 */
@Component
public class DlxConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(DlxConsumer.class);

    @RabbitListener(queues = DlxConfig.DLX_QUEUE_01)
    public void handleDlxMessage(String message){
        LOGGER.info("DlxMessage from " + DlxConfig.DLX_QUEUE_01 + " : " + message);
    }
}
