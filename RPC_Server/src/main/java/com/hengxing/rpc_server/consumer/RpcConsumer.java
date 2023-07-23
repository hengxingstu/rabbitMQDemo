package com.hengxing.rpc_server.consumer;

import com.hengxing.rpc_server.config.RpcConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author hengxing
 * @version 1.0
 * @project RabbitMQ_demo
 * @date 7/13/2023 20:50:33
 */
@Component
public class RpcConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcConsumer.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 接收消息，获取correlationId，在返回消息时附带上这个id
     */
    @RabbitListener(queues = RpcConfig.MESSAGE_QUEUE)
    public void handleMessage(Message message){
        LOGGER.info("Server receive: " + new String(message.getBody()));
        Message response = MessageBuilder.withBody(("I'm receive:" + new String(message.getBody())).getBytes()).build();
        CorrelationData correlationData = new CorrelationData(message.getMessageProperties().getCorrelationId());
        rabbitTemplate.sendAndReceive(RpcConfig.RPC_EXCHANGE,RpcConfig.REPLY_QUEUE,response,correlationData);
    }
}
