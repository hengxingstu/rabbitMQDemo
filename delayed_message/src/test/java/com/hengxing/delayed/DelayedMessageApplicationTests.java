package com.hengxing.delayed;

import com.hengxing.delayed.config.DelayedConfig;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
class DelayedMessageApplicationTests {

	@Autowired
	RabbitTemplate rabbitTemplate;

	@Test
	void contextLoads() {
	}

	@Test
	public void testDelayed() {
		Message message = MessageBuilder.withBody(("hello hengxing." + new Date()).getBytes())
				.setHeader("x-delay", 3000)//设置过期时间，必须使用这个参数在消息头设置
				.build();
		rabbitTemplate.convertAndSend(DelayedConfig.DELAYED_EXCHANGE_NAME,DelayedConfig.DELAYED_QUEUE_NAME,message);

	}

}
