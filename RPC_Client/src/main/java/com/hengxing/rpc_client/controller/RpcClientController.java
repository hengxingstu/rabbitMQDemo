package com.hengxing.rpc_client.controller;

import com.hengxing.rpc_client.config.RPCConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 提交这个代码
 * @author hengxing
 * @version 1.0
 * @project RabbitMQ_demo
 * @date 7/13/2023 20:17:59
 */
@RestController
public class RpcClientController {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcClientController.class);

    @Autowired
    RabbitTemplate rabbitTemplate;

    @GetMapping("/send")
    public String send(String message){
        //创建消息
        Message newMessage = MessageBuilder.withBody(message.getBytes()).build();
        LOGGER.info("client send: " + newMessage);

        //发送消息
        Message result = rabbitTemplate.sendAndReceive(RPCConfig.RPC_EXCHANGE, RPCConfig.MESSAGE_QUEUE, newMessage);

        String response = "";
        if (result != null) {
            //获取已发送的消息的correlationId
            String correlationId = newMessage.getMessageProperties().getCorrelationId();
            LOGGER.info("correlationId: " + correlationId);

            //获取响应头信息
            Map<String, Object> headers = result.getMessageProperties().getHeaders();
            String returned_correlation_id = (String) headers.get("spring_returned_message_correlation");
            if (returned_correlation_id.equals(correlationId)){
                response = new String(result.getBody());
                LOGGER.info("Message from server:" + response);
            }
        }
        return response;
    }
}
