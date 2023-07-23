package com.hengxing.consumer01;

import com.hengxing.consumer01.config.RabbitMQConfig;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class Consumer01ApplicationTests {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Test
    void contextLoads() {
        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME,"你好恒星！");
        String o = (String) rabbitTemplate.receiveAndConvert(RabbitMQConfig.QUEUE_NAME);
        System.out.println("消息：" + o);
    }

    @Transactional
    @Test
    public void testTransaction() {
        //打开信道的事务模式
        rabbitTemplate.setChannelTransacted(true);
        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME,"hello rabbitMQ");
        int i = 1 / 0;
    }

}
