package com.hengxing.produce02.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author hengxing
 * @version 1.0
 * @project RabbitMQ_demo
 * @date 7/16/2023 16:51:55
 */
@Configuration
public class RabbitConfig implements RabbitTemplate.ReturnsCallback, RabbitTemplate.ConfirmCallback {
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitConfig.class);

    @Autowired
    RabbitTemplate rabbitTemplate;

    public static final String EXCHANGE_NAME = "exchange_01";
    public static final String QUEUE_NAME = "queue_01";

    /**
     * @PostConstruct 当构造方法执行完毕，会执行这个方法
     */
    @PostConstruct
    public void postInit(){
        rabbitTemplate.setReturnsCallback(this);
        rabbitTemplate.setConfirmCallback(this);
    }

    /**
     * 消息没有到达消息队列，就会触发这个方法，参数中包含发送的消息
     * 如果成功到达了，就不会被触发
     * @param returned 返回信息
     * @return void
     * @author hengxing
     * @date 7/16/2023 17:14:46
     */
    @Override
    public void returnedMessage(ReturnedMessage returned) {
        byte[] msgBytes = returned.getMessage().getBody();
        LOGGER.info("消息未到达消息队列：message:{};exchange:{};queue:{}",new String(msgBytes),returned.getExchange(),returned.getRoutingKey());
    }

    /**
     * 消息到达交换机的回调
     * @param correlationData 这个参数中有一个correlationId，可以用来识别消息
	 * @param ack 是否收到消息
	 * @param cause 失败原因
     * @return void
     * @author hengxing
     * @date 7/16/2023 17:10:07
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        LOGGER.info("消息到达交换机：ack:{};cause:{};correlationId:{};",ack,cause,correlationData.getId());
    }

    @Bean
    Queue queue(){
        return new Queue(QUEUE_NAME,true,false,false);
    }

    @Bean
    DirectExchange exchange(){
        return new DirectExchange(EXCHANGE_NAME,true,false);
    }

    @Bean
    Binding binding(){
        return BindingBuilder.bind(queue())
                .to(exchange())
                .with(QUEUE_NAME);
    }
}
