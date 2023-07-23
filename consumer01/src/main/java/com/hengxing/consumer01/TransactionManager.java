package com.hengxing.consumer01;


import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.transaction.RabbitTransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author hengxing
 * @version 1.0
 * @project RabbitMQ_demo
 * @date 7/16/2023 10:54:43
 */
@Component
public class TransactionManager {

    //提供一个事务管理器
    @Bean
    RabbitTransactionManager rabbitTransationManager(ConnectionFactory connectionFactory){
        return new RabbitTransactionManager(connectionFactory);
    }
}
