package com.hengxing.producer01;

import com.hengxing.producer01.config.*;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class Producer01ApplicationTests {
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Test
    void contextLoads() {
    }

    @Test
    public void testDLXMessage() {
        rabbitTemplate.convertAndSend(TtlConfig.TTL_EXCHANGE_NAME,TtlConfig.TTL_QUEUE_02,"hello DLXMessage");
    }
    @Test
    public void testTTLMessage() {
        Message message = MessageBuilder.withBody("hello TTL".getBytes())
                .setExpiration("10000").build();
        rabbitTemplate.send(TtlConfig.TTL_EXCHANGE_NAME,TtlConfig.TTL_QUEUE_01,message);

        //给设置ttl的队列发消息
        rabbitTemplate.convertAndSend(TtlConfig.TTL_EXCHANGE_NAME,TtlConfig.TTL_QUEUE_02,"hello ttl");
    }

    @Test
    public void testHeaderMessage() {
        Message nameMessage = MessageBuilder.withBody("hello name".getBytes())
                .setHeader("name", null).build();
        Message ageMessage = MessageBuilder.withBody("hello age".getBytes())
                .setHeader("age", 99).build();
        rabbitTemplate.send(HeaderConfig.HEADER_EXCHANGE_NAME,null,nameMessage);
        rabbitTemplate.send(HeaderConfig.HEADER_EXCHANGE_NAME,null,ageMessage);
    }

    @Test
    public void testTopicMessage() {
        rabbitTemplate.convertAndSend(TopicConfig.TOPIC_EXCHANGE_NAME,"xiaomi.news","小米新闻");
        rabbitTemplate.convertAndSend(TopicConfig.TOPIC_EXCHANGE_NAME,"huawei.news","华为新闻");
        rabbitTemplate.convertAndSend(TopicConfig.TOPIC_EXCHANGE_NAME,"xiaomi.phone.news","小米手机新闻");
    }

    @Test
    public void testMessage() {

        for (int i = 0; i < 10; i++) {
            rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, "你好 恒星！" + i);
        }
    }

    @Test
    public void testExchange() {
        rabbitTemplate.convertAndSend(ExchangeConfig.QUEUE_NAME_01,"hello hengxing01");
        rabbitTemplate.convertAndSend(ExchangeConfig.QUEUE_NAME_02,"hello hengxing02");
    }

    @Test
    public void testFanOutExchange() {
        //类似广播模式
        rabbitTemplate.convertAndSend(FanOutConfig.EXCHANGE_NAME,null,"hello FanOut Hengxing01");
        rabbitTemplate.convertAndSend(FanOutConfig.EXCHANGE_NAME,null,"hello FanOut Hengxing02");
    }

}
