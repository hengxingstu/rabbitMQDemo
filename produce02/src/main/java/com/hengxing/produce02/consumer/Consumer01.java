package com.hengxing.produce02.consumer;

import com.hengxing.produce02.config.RabbitConfig;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.impl.AMQChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author hengxing
 * @version 1.0
 * @project RabbitMQ_demo
 * @date 7/16/2023 17:29:18
 */
@Component
public class Consumer01 {

    private static final Logger LOGGER = LoggerFactory.getLogger(Consumer01.class);

//    @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
//    public void handle(String message){
//        LOGGER.info("message:" + message);
//    }

    @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
    public void handle(Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            LOGGER.info("message:" + new String(message.getBody()));
            int i = 1 / 0;
            //消费完成后，手动ack
            channel.basicAck(deliveryTag,false);
        } catch (Exception e) {
            e.printStackTrace();
            //最后一个参数为是否重新回队列
            try {
                channel.basicNack(deliveryTag,false,true);
                //拒绝消费
//                channel.basicReject(deliveryTag,false);
            } catch (IOException ex){
                ex.printStackTrace();
            }

        }
    }

}
