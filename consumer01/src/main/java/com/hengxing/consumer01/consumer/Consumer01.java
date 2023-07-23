package com.hengxing.consumer01.consumer;

import com.hengxing.consumer01.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;


/**
 * @author hengxing
 * @version 1.0
 * @project RabbitMQ_demo
 * @date 7/8/2023 17:34:03
 */
@Component
public class Consumer01 {

    private static final Logger LOGGER = LoggerFactory.getLogger(Consumer01.class);

//    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME,concurrency = "10")
//    public void handleMessage(String msg){
//        LOGGER.info("来自RabbitMQ的消息：" + msg);
//    }
//    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
//    public void handleMessage2(String msg){
//        LOGGER.info("来自RabbitMQ的消息2：" + msg);
//    }
}
