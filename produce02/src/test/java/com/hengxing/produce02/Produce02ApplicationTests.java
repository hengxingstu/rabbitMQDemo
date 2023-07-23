package com.hengxing.produce02;

import com.hengxing.produce02.config.RabbitConfig;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class Produce02ApplicationTests {
	@Autowired
	RabbitTemplate rabbitTemplate;

	@Test
	void contextLoads() {
		rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_NAME,RabbitConfig.QUEUE_NAME,"一条消息",new CorrelationData("99"));
	}

}
