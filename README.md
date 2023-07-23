# RabbitMQ笔记速查

### MQ消息队列七种模型

#### 一、直连交换机

消费者设置

```java
@Configuration
public class ExchangeConfig {
    /*队列和交换机名称*/
    public static final String QUEUE_NAME_01 = "hengxing01";
    public static final String QUEUE_NAME_02 = "hengxing02";
    public static final String EXCHANGE_NAME = "MyExchange";

    /* 创建队列*/
    @Bean
    Queue queue01(){
        return new Queue(QUEUE_NAME_01,true,false,false);
    }

    @Bean
    Queue queue02(){
        return new Queue(QUEUE_NAME_02,true,false,false);
    }

    /* 创建交换机*/
    @Bean
    DirectExchange MyExchange(){
        return new DirectExchange(EXCHANGE_NAME,true,false);
    }

    /* 绑定对应交换机和队列*/
    @Bean
    Binding binding01(){
        return BindingBuilder.bind(queue01())
                .to(MyExchange())
                .with(QUEUE_NAME_01);
    }

    @Bean
    Binding binding02(){
        return BindingBuilder.bind(queue02())
                .to(MyExchange())
                .with(QUEUE_NAME_02);
    }

}
```

定义消费者

```java
@Component
public class DirectConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DirectConsumer.class);

    @RabbitListener(queues = ExchangeConfig.QUEUE_NAME_01)
    public void handleMessage(String message){
        LOGGER.info(ExchangeConfig.QUEUE_NAME_01 + ":" + message);
    }

    @RabbitListener(queues = ExchangeConfig.QUEUE_NAME_02)
    public void handleMessage02(String message){
        LOGGER.info(ExchangeConfig.QUEUE_NAME_02 + ":" + message);
    }
}
```

定义生产者给交换机发送消息，定义`routingKey`

```java
@Test
    public void testExchange() {
        rabbitTemplate.convertAndSend(ExchangeConfig.QUEUE_NAME_01,"hello hengxing01");
        rabbitTemplate.convertAndSend(ExchangeConfig.QUEUE_NAME_02,"hello hengxing02");
    }
```

交换机会根据`routingKey`的不同，发送到对应的队列中去

#### 二、扇形交换机

扇形的原点是交换机，边上是一圈消费者节点。当收到消息时，交换机会广播消息到所有连接的节点上。

设置类，还是相同的套路，先配置交换机和队列，再分别绑定

```java
@Configuration
public class FanOutConfig {
    public static final String QUEUE_NAME_01 = "FanOuthengxing01";
    public static final String QUEUE_NAME_02 = "FanOuthengxing02";
    public static final String EXCHANGE_NAME = "FanOutExchange";

    @Bean
    FanoutExchange fanoutExchange(){
        return new FanoutExchange(EXCHANGE_NAME,true,false);
    }

    @Bean
    Queue fanQueue01(){
        return new Queue(QUEUE_NAME_01,true,false,false);
    }

    @Bean
    Queue fanQueue02(){
        return new Queue(QUEUE_NAME_02,true,false,false);
    }

    @Bean
    Binding FanOutBinding01(){
        return BindingBuilder.bind(fanQueue01())
                .to(fanoutExchange());
    }

    @Bean
    Binding FanOutBinding02(){
        return BindingBuilder.bind(fanQueue02())
                .to(fanoutExchange());
    }
}
```

配置消费者，消费者需要指定自己监听的队列

```java
@Component
public class FanOutConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(FanOutConsumer.class);

    @RabbitListener(queues = FanOutConfig.QUEUE_NAME_01)
    public void consumer01(String message){
        LOGGER.info(FanOutConfig.QUEUE_NAME_01 + ":" + message);
    }

    @RabbitListener(queues = FanOutConfig.QUEUE_NAME_02)
    public void consumer02(String message){
        LOGGER.info(FanOutConfig.QUEUE_NAME_02 + ":" + message);
    }
}
```



结果是，两边都收到了信息。证明其确实广播了信息到所有连接到的消费者节点上

```
2023-07-10 11:06:30.569  INFO 15844 --- [ntContainer#2-1] c.h.consumer02.consumer.FanOutConsumer   : FanOuthengxing01:hello FanOut Hengxing01
2023-07-10 11:06:30.569  INFO 15844 --- [ntContainer#3-1] c.h.consumer02.consumer.FanOutConsumer   : FanOuthengxing02:hello FanOut Hengxing01
2023-07-10 11:06:30.570  INFO 15844 --- [ntContainer#2-1] c.h.consumer02.consumer.FanOutConsumer   : FanOuthengxing01:hello FanOut Hengxing02
2023-07-10 11:06:30.570  INFO 15844 --- [ntContainer#3-1] c.h.consumer02.consumer.FanOutConsumer   : FanOuthengxing02:hello FanOut Hengxing02
```

#### 三、TOPIC交换机

这个就是在直连交换机的基础上，支持`routingKey`中输入通配符。允许我们根据需要匹配消息队列。

设置主题交换机，分别设置队列名称。重点是设置routingKey，第三个phone可以接收所有routingKey中包含phone的消息

```java
@Configuration
public class TopicConfig {

    public static final String TOPIC_EXCHANGE_NAME = "Topic_Exchange";
    public static final String HUAWEI_NEWS_QUEUE01 = "huawei_news_queue01";
    public static final String XIAOMI_NEWS_QUEUE01 = "xiaomi_news_queue01";
    public static final String XIAOMI_PHONE_NEWS_QUEUE01 = "xiaomi_phone_news_queue01";

    @Bean
    Queue huaweiQueue(){
        return new Queue(HUAWEI_NEWS_QUEUE01,true,false,false);
    }

    @Bean
    Queue xiaomiQueue(){
        return new Queue(XIAOMI_NEWS_QUEUE01,true,false,false);
    }

    @Bean
    Queue xiaomiPhoneQueue(){
        return new Queue(XIAOMI_PHONE_NEWS_QUEUE01,true,false,false);
    }

    @Bean
    TopicExchange topicExchange(){
        return new TopicExchange(TOPIC_EXCHANGE_NAME,true,false);
    }

    @Bean
    Binding xiaomiBinding(){
        return BindingBuilder.bind(xiaomiQueue())
                .to(topicExchange())
                .with("xiaomi.#");
        //# 表示通配符，如果routingKey是xiaomi.xxx，那么这个消息就会被转换到xiaomiQueue队列中
    }

    @Bean
    Binding huaweiBinding(){
        return BindingBuilder.bind(huaweiQueue())
                .to(topicExchange())
                .with("huawei.#");
    }

    @Bean
    Binding phoneBinding(){
        return BindingBuilder.bind(xiaomiPhoneQueue())
                .to(topicExchange())
                .with("#.phone.#");
    }
}
```

主题消费者

```java
@Component
public class TopicConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(TopicConsumer.class);

    @RabbitListener(queues = TopicConfig.HUAWEI_NEWS_QUEUE01)
    public void handleMessage01(String message){
        LOGGER.info(TopicConfig.HUAWEI_NEWS_QUEUE01 + ":" + message);
    }

    @RabbitListener(queues = TopicConfig.XIAOMI_NEWS_QUEUE01)
    public void handleMessage02(String message){
        LOGGER.info(TopicConfig.XIAOMI_NEWS_QUEUE01 + ":" + message);
    }

    @RabbitListener(queues = TopicConfig.XIAOMI_PHONE_NEWS_QUEUE01)
    public void handleMessage03(String message){
        LOGGER.info(TopicConfig.XIAOMI_PHONE_NEWS_QUEUE01 + ":" + message);
    }
}
```

结果：

```
 xiaomi_news_queue01:小米新闻
 huawei_news_queue01:华为新闻
xiaomi_phone_news_queue01:小米手机新闻
xiaomi_news_queue01:小米手机新闻
```

#### 四、头部交换机

HeaderExchange，就是在header中保存了固定信息，每次发送时，对比header，如果信息匹配，就发送到对应的队列

```java
@Configuration
public class HeaderConfig {

    public static final String HEADER_EXCHANGE_NAME = "header_exchange01";
    public static final String HEADER_QUEUE_01 = "header_queue_01";
    public static final String HEADER_QUEUE_02 = "header_queue_02";

    @Bean
    Queue headerQueue01(){
        return new Queue(HEADER_QUEUE_01,true,false,false);
    }

    @Bean
    Queue headerQueue02(){
        return new Queue(HEADER_QUEUE_02,true,false,false);
    }

    @Bean
    HeadersExchange headersExchange(){
        return new HeadersExchange(HEADER_EXCHANGE_NAME,true,false);
    }

    @Bean
    Binding headerBinding01(){
        return BindingBuilder.bind(headerQueue01())
                .to(headersExchange())
                //头部信息中有name属性的消息，会被接收
                .where("name").exists();
    }

    @Bean
    Binding headerBinding02(){
        return BindingBuilder.bind(headerQueue02())
                .to(headersExchange())
                //头部信息中有age属性，并且值为99 的消息，会被接收
                .where("age").matches(99);
    }
}
```

重点在header交换机的头部信息，第一个我设置为`.where("name").exists()`，意味着所有头部信息中有name属性的消息，都会被捕获。第二个我设置为` .where("age").matches(99)`，意味着头部有age属性并且值为99的消息会被捕获。

```java
@Component
public class HeaderConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(HeaderConsumer.class);

    @RabbitListener(queues = HeaderConfig.HEADER_QUEUE_01)
    public void handleMessage01(byte[] message){
        LOGGER.info(HeaderConfig.HEADER_QUEUE_01 + ": " + new String(message));
    }

    @RabbitListener(queues = HeaderConfig.HEADER_QUEUE_02)
    public void handleMessage02(byte[] message){
        LOGGER.info(HeaderConfig.HEADER_QUEUE_02 + ": " + new String(message));
    }
}
```



消费者还是老一套，绑定对应的队列即可，这里需要注意的是，我们写头部信息时只能使用`MessageBuilder`去构建消息，这个方法发出的消息会以byte数组形式传输，所以接收的时候也必须是byte数组。在下方是给消息设置header信息的代码。

```java
@Test
public void testHeaderMessage() {
    Message nameMessage = MessageBuilder.withBody("hello name".getBytes())
        .setHeader("name", null).build();
    Message ageMessage = MessageBuilder.withBody("hello age".getBytes())
        .setHeader("age", 99).build();
    rabbitTemplate.send(HeaderConfig.HEADER_EXCHANGE_NAME,null,nameMessage);
    rabbitTemplate.send(HeaderConfig.HEADER_EXCHANGE_NAME,null,ageMessage);
}
```

### MQ实现RPC调用

RPC（Remote Procedure Call Protocol 远程过程调用协议）

#### 客户端配置

参数配置

```properties
spring.rabbitmq.host=centos
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
#带上这个参数，才会在消息发送时带上correlationId
spring.rabbitmq.publisher-confirm-type=correlated
#这个表示发送失败时会触发一个回调方法
spring.rabbitmq.publisher-returns=true
```



```java
@Configuration
public class RPCConfig {
    public static final String MESSAGE_QUEUE = "message_queue";
    public static final String REPLY_QUEUE = "reply_queue";
    public static final String RPC_EXCHANGE = "rpc_exchange";

    @Bean
    Queue messageQueue(){
        return new Queue(MESSAGE_QUEUE,true,false,false);
    }

    @Bean
    Queue replyQueue(){
        return new Queue(REPLY_QUEUE,true,false,false);
    }

    @Bean
    DirectExchange RPCExchange(){
        return new DirectExchange(RPC_EXCHANGE,true,false);
    }

    //请求队列和交换器绑定
    @Bean
    Binding RPCBinding(){
        return BindingBuilder.bind(messageQueue())
                .to(RPCExchange())
                .with(MESSAGE_QUEUE);
    }

    //返回队列和交换器绑定
    @Bean
    Binding replyBinding(){
        return BindingBuilder.bind(replyQueue())
                .to(RPCExchange())
                .with(REPLY_QUEUE);
    }

    /**
     * 由rabbitTemplate发送和接收消息
     * 设置回调队列地址
     */
    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setReplyAddress(REPLY_QUEUE);
        template.setReplyTimeout(6000);
        return template;
    }

    @Bean
    SimpleMessageListenerContainer replyContainer(ConnectionFactory connectionFactory){
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(REPLY_QUEUE);
        container.setMessageListener(rabbitTemplate(connectionFactory));
        return container;
    }
}
```

这个类配置消息发送对列`messageQueue`和消息返回队列`replyQueue`，然后分别绑定交换机。

然后通过rabbitTemplate去发送消息，这里需要对工具进行定义，给消息发送添加一个返回队列，然后给返回队列设置监听。==以接收返回消息==

现在开始消息发送，我们写一个controller来控制消息发送

```java
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
```

这块的代码其实也都是一些常规代码，我挑几个关键的节点说下：

1. 消息发送调用`sendAndReceive`方法，该方法自带返回值，返回值就是服务端返回的消息。
2. 服务端返回的消息中，头信息中包含了`spring_returned_message_correlation`字段，这个就是消息发送时候的 correlation_id，通过消息发送时候的 correlation_id 以及返回消息头中的`spring_returned_message_correlation`字段值，我们就可以将返回的消息内容和发送的消息绑定到一起，确认出这个返回的内容就是针对这个发送的消息的。

这就是整个客户端的开发，其实最最核心的就是`sendAndReceive`方法的调用。调用虽然简单，但是准备工作还是要做足够。例如如果我们没有在`application.properties`中配置`correlated`，发送的消息中就没有`correlation_id`，这样就无法将返回的消息内容和发送的消息内容关联起来。

#### 服务端配置

```java
@Configuration
public class RpcConfig {

    public static final String MESSAGE_QUEUE = "message_queue";
    public static final String REPLY_QUEUE = "reply_queue";
    public static final String RPC_EXCHANGE = "RPC_EXCHANGE";

    //消息发送队列
    @Bean
    Queue messageQueue(){
        return new Queue(MESSAGE_QUEUE,true,false,false);
    }

    //消息返回队列
    @Bean
    Queue replyQueue(){
        return new Queue(REPLY_QUEUE,true,false,false);
    }

    @Bean
    DirectExchange directExchange(){
        return new DirectExchange(RPC_EXCHANGE);
    }

    //绑定请求队列
    @Bean
    Binding messageBinding(){
        return BindingBuilder.bind(messageQueue()).to(directExchange()).with(MESSAGE_QUEUE);
    }

    //绑定返回队列
    @Bean
    Binding replyBinding(){
        return BindingBuilder.bind(replyQueue()).to(directExchange()).with(REPLY_QUEUE);
    }
}
```

然后对消息进行消费

```java
@Component
public class RpcConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcConsumer.class);
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    /**
     * 接收消息，获取correlationId，在返回消息时附带上这个id
     */
    @RabbitListener
    public void handleMessage(Message message){
        LOGGER.info("Server receive: " + new String(message.getBody()));
        Message response = MessageBuilder.withBody(("I'm receive:" + new String(message.getBody())).getBytes()).build();
        CorrelationData correlationData = new CorrelationData(message.getMessageProperties().getCorrelationId());
        rabbitTemplate.sendAndReceive(RpcConfig.RPC_EXCHANGE,RpcConfig.REPLY_QUEUE,response,correlationData);
    }
}
```

### 设置消息过期时间

TTL = Time To Live ，超过此时间还没消费的消息，就会变成死信

#### 两种方式

1. 为队列声明消息有效期，所有进入队列的消息都有相同的有效期
2. 发消息时给消息设置

==两个都设置，就以时间短的为准==

两种方式对应的删除时机有一些差异：

1. 对于第一种方式，当消息队列设置过期时间的时候，那么消息过期了就会被删除，因为消息进入 RabbitMQ 后是存在一个消息队列中，队列的头部是最早要过期的消息，所以 RabbitMQ 只需要一个定时任务，从头部开始扫描是否有过期消息，有的话就直接删除。
2. 对于第二种方式，当消息过期后并不会立马被删除，而是当消息要投递给消费者的时候才会去删除，因为第二种方式，每条消息的过期时间都不一样，想要知道哪条消息过期，必须要遍历队列中的所有消息才能实现，当消息比较多时这样就比较耗费性能，因此对于第二种方式，当消息要投递
   给消费者的时候才去删除。

#### 示例

- 为==队列==设置过期时间，就是在创建队列时，设置参数`x-message-ttl`。

```java
@Bean
Queue TtlQueue02(){
    Map<String, Object> arguments = new HashMap<>();
    int second = 1000;
    arguments.put("x-message-ttl",10 * second);
    return new Queue(TTL_QUEUE_02,true,false,false,arguments);
}

```

发送时直接发送正常的消息，当然携带上ttl参数的消息，在这个参数小于队列的ttl才会生效。

- 为==消息==设置过期时间，就是在发送消息时带上，过期时间的参数。

```java
Message message = MessageBuilder.withBody("hello TTL".getBytes())
                .setExpiration("10000").build();
        rabbitTemplate.send(TtlConfig.TTL_EXCHANGE_NAME,TtlConfig.TTL_QUEUE_01,message);
```

#### 完整配置代码

```java
package com.hengxing.producer01.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author hengxing
 * @version 1.0
 * @project RabbitMQ_demo
 * @date 7/15/2023 15:45:22
 */
@Configuration
public class TtlConfig {
    public static final String TTL_EXCHANGE_NAME = "ttl_exchange";
    public static final String TTL_QUEUE_01 = "ttl_queue_01";
    public static final String TTL_QUEUE_02 = "ttl_queue_02";

    @Bean
    DirectExchange TTLDirectExchange(){
        return new DirectExchange(TTL_EXCHANGE_NAME, true, false);
    }

    @Bean
    Queue TtlQueue01(){
        return new Queue(TTL_QUEUE_01,true,false,false);
    }

    @Bean
    Queue TtlQueue02(){
        Map<String, Object> arguments = new HashMap<>();
        int second = 1000;
        arguments.put("x-message-ttl",10 * second);
        return new Queue(TTL_QUEUE_02,true,false,false,arguments);
    }

    @Bean
    Binding TtlBinding01(){
        return BindingBuilder.bind(TtlQueue01())
                .to(TTLDirectExchange())
                .with(TTL_QUEUE_01);
    }

    @Bean
    Binding TtlBinding02(){
        return BindingBuilder.bind(TtlQueue02())
                .to(TTLDirectExchange())
                .with(TTL_QUEUE_02);
    }
}
```

### 死信消息和死信队列

消息过期之后，不会立即删除，而是被转发到死信交换机，死信交换机再转发到对应的消息队列中去。

死信队列和正常的队列没什么区别

死信交换机，==Dead-Letter-Exchange== 即 DLX

死信交换机用来接收死信消息（Dead Message）的，那什么是死信消息呢？一般消息变成死信消息有如下几种情况：

- 消息被拒绝(Basic.Reject/Basic.Nack) ，井且设置requeue 参数为false
- 消息过期
- 队列达到最大长度

1. 首先需要配置死信队列和死信交换机

   ```java
   @Configuration
   public class DlxConfig {
       public static final String DLX_EXCHANGE_NAME = "dlx_exchange_name";
       public static final String DLX_QUEUE_01 = "dlx_queue_01";
   
       @Bean
       DirectExchange DLXExchange(){
           return new DirectExchange(DLX_EXCHANGE_NAME,true,false);
       }
   
       @Bean
       Queue dlxQueue(){
           return new Queue(DLX_QUEUE_01,true,false,false);
       }
       
       @Bean
       Binding binding(){
           return BindingBuilder.bind(dlxQueue())
                   .to(DLXExchange())
                   .with(DLX_QUEUE_01);
       }
   }
   ```

2. 设置死信交换机的消费者，你可以在这个队列里面设置想要的消费动作

   ```java
   @Component
   public class DlxConsumer {
       private static final Logger LOGGER = LoggerFactory.getLogger(DlxConsumer.class);
   
       @RabbitListener(queues = DlxConfig.DLX_QUEUE_01)
       public void handleDlxMessage(String message){
           LOGGER.info("DlxMessage from " + DlxConfig.DLX_QUEUE_01 + " : " + message);
       }
   }
   ```

3. 配置某个队列的消息失败后，转到对应的死信队列

   在这个场景中，消息会先发送给正常队列，队列如果没有在消息过期之前消费，导致消息过期，就会转到死信交换机`DLX_EXCHANGE_NAME`，再转到对应的队列`DLX_QUEUE_01`

   重要的是这三个参数

   - x-message-ttl  //消息过期时间
   - x-dead-letter-exchange //转发到的死信交换机
   - x-dead-letter-routing-key  //发到哪个死信队列？

   ```java
   @Configuration
   public class TtlConfig {
       public static final String TTL_EXCHANGE_NAME = "ttl_exchange";
       public static final String TTL_QUEUE_01 = "ttl_queue_01";
       public static final String TTL_QUEUE_02 = "ttl_queue_02";
   
       @Bean
       DirectExchange TTLDirectExchange(){
           return new DirectExchange(TTL_EXCHANGE_NAME, true, false);
       }
   
       @Bean
       Queue TtlQueue01(){
           return new Queue(TTL_QUEUE_01,true,false,false);
       }
   
       @Bean
       Queue TtlQueue02(){
           Map<String, Object> arguments = new HashMap<>();
           arguments.put("x-message-ttl",10000);//消息过期时间
           arguments.put("x-dead-letter-exchange",DlxConfig.DLX_EXCHANGE_NAME);//发到死信交换机
           arguments.put("x-dead-letter-routing-key",DlxConfig.DLX_QUEUE_01);//发到哪个死信队列？
           return new Queue(TTL_QUEUE_02,true,false,false,arguments);
       }
   
       @Bean
       Binding TtlBinding01(){
           return BindingBuilder.bind(TtlQueue01())
                   .to(TTLDirectExchange())
                   .with(TTL_QUEUE_01);
       }
   
       @Bean
       Binding TtlBinding02(){
           return BindingBuilder.bind(TtlQueue02())
                   .to(TTLDirectExchange())
                   .with(TTL_QUEUE_02);
       }
   }
   ```

   > #### 辨析
   >
   > 其实你可以发现，死信队列和正常的队列没有什么区别，无非就是正常的队列消息过期之后，集中处理的地方。
   >
   > 不过利用这个特性，我们可以将其当作一个==延时队列==来使用，队列中的消息在过期之后，集中到死信队列中去处理。

### 利用插件来实现延时队列


虽然死信队列就可以做到这个功能，但是配置起来比较麻烦，需要至少两个队列才能完成，有没有天生支持延时的队列呢？

可以用第三方插件来完成

https://github.com/rabbitmq/rabbitmq-delayed-message-exchange/releases

#### 第一步 安装插件

我下载的是最新版

`wget https://github.com/rabbitmq/rabbitmq-delayed-message-exchange/releases/download/v3.12.0/rabbitmq_delayed_message_exchange-3.12.0.ez`

再把文件拷贝到对应容器中去

`docker cp rabbitmq_delayed_message_exchange-3.12.0.ez tender_lalande:/plugins`

可以先查看一下是否有这个插件

`rabbitmq-plugins list`

启动对应插件

`rabbitmq-plugins enable rabbitmq_delayed_message_exchange`

然后可以重新查看插件列表，如果看到自己的插件前多了`E*`，说明启动成功

`[E*] rabbitmq_delayed_message_exchange 3.12.0`

#### 第二步 配置项目

对应的依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

项目参数

```properties
spring.rabbitmq.host=centos
spring.rabbitmq.password=guest
spring.rabbitmq.username=guest
spring.rabbitmq.virtual-host=/
```

消息队列的配置

```java
@Configuration
public class DelayedConfig {

    public static final String DELAYED_EXCHANGE_NAME = "delayed_exchange";
    public static final String DELAYED_EXCHANGE_TYPE = "x-delayed-message";//这个参数声明其是一个延迟队列
    public static final String DELAYED_QUEUE_NAME = "delayed_queue";

    @Bean
    Queue delayedQueue(){
        return new Queue(DELAYED_QUEUE_NAME,true,false,false);
    }

    @Bean
    CustomExchange delayedExchange(){
        Map<String, Object> argument = new HashMap<>();
        argument.put("x-delayed-type","direct");//声明这是一个直连交换机，四种交换机都可选
        return new CustomExchange(DELAYED_EXCHANGE_NAME,DELAYED_EXCHANGE_TYPE,true,false,argument);
    }

    @Bean
    Binding binding(){
        return BindingBuilder.bind(delayedQueue())
                .to(delayedExchange())
                .with(DELAYED_QUEUE_NAME)
                .noargs();//这个无参是CustomExchange这种交换机特殊需求
    }
}
```

这里我们使用的交换机是 CustomExchange，这是一个 Spring 中提供的交换机，创建CustomExchange 时有五个参数，含义分别如下：

- 交换机名称
- 交换机类型，这个地方是固定的
- 交换机是否持久化
- 如果没有队列绑定到交换机，交换机是否删除。
- 其他参数

最后一个 args 参数中，指定了交换机消息分发的类型，这个类型就是大家熟知的 direct、fanout、topic 以及 header 几种，用了哪种类型，将来交换机分发消息就按哪种方式来。



#### 第三步 创建消费者

这里我们可以打印一下时间，看到底延时了多久

```java
@Component
public class DelayedConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DelayedConsumer.class);

    @RabbitListener(queues = DelayedConfig.DELAYED_QUEUE_NAME)
    public void handelDelayed(String message){
        LOGGER.info("message from Delayed:" + message + "-->" + new Date());
    }
}
```

#### 第四步 发送延时消息

```java
@Component
public class DelayedConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DelayedConsumer.class);

    @RabbitListener(queues = DelayedConfig.DELAYED_QUEUE_NAME)
    public void handelDelayed(String message){
        LOGGER.info("message from Delayed:" + message + "-->收到信息的时间" + new Date());
    }
}
```

我们收到了消息，延时确实是3秒：

```
2023-07-15 20:21:27.346  INFO 10592 --- [ntContainer#0-1] c.h.delayed.consumer.DelayedConsumer     : message from Delayed:hello hengxing.Sat Jul 15 20:21:24 CST 2023-->Sat Jul 15 20:21:27 CST 2023
```



### 确保消息发送可靠性

先来看消息发送的流程

![rabbitmq消息发送机制](https://s2.loli.net/2023/07/16/K4spf7tglXBO9w3.png)

大致的流程就是这样，所以要确保消息发送的可靠性，主要从两方面去确认：

1. 消息成功到达 Exchange
2. 消息成功到达 Queue

要确保消息成功发送，我们只需要做好三件事就可以了：

1. 确认消息到达 Exchange
2. 确认消息到达 Queue
3. 开启定时任务，定时投递那些发送失败的消息

一般来说，第一步出问题的可能性较高，因为网络原因，消息发送失败。但是第二步在系统内部，发送失败只可能是代码出错，或者消息队列挂掉了。第三步需要我们自己实现。

所以当下我们只需要考虑第一步的问题，在这里RabbitMQ给了我们两种现成的方案。

1. 开启事务机制
2. 发送方确认机制

二者任选其一，不可同时开启。



#### 一、通过事务

首先需要提供一个事务管理器

```java
@Bean
RabbitTransactionManager rabbitTransationManager(ConnectionFactory connectionFactory){
    return new RabbitTransactionManager(connectionFactory);
}
```

然后在发送消息时加上事务注释，开启信道模式为事务模式，即可

```java
@Transactional
@Test
public void testTransaction() {
    //打开信道的事务模式
    rabbitTemplate.setChannelTransacted(true);
    rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME,"hello rabbitMQ");
    int i = 1 / 0;
}
```

在上面的案例中，我们在结尾来了个 1/0 ，这在运行时必然抛出异常，我们可以尝试运行该方法，发现消息并未发送成功。

当我们开启事务模式之后，RabbitMQ 生产者发送消息会多出四个步骤：

1. 客户端发出请求，将信道设置为事务模式
2. 服务端给出回复，同意将信道设置为事务模式
3. 客户端发送消息
4. 客户端提交事务
5. 服务端给出响应，确认事务提交

上面的步骤，除了第三步是本来就有的，其他几个步骤都是平白无故多出来的。所以大家看到，事务模式其实效率有点低，这并非一个最佳解决方案。我们可以想想，什么项目会用到消息中间件？一般来说都是一些高并发的项目，这个时候并发性能尤为重要。

#### 二、通过消息确认机制

##### 单条消息处理

在 application.properties 中配置开启消息发送方确认机制

```properties
spring.rabbitmq.publisher-confirm-type=correlated
spring.rabbitmq.publisher-returns=true
```

第一行是配置消息到达交换器的确认回调，第二行则是配置消息到达队列的回调。

第一行属性的配置有三个取值：

1. `none`：表示禁用发布确认模式，默认即此
2. `correlated`：表示成功发布消息到交换器后会触发的回调方法
3. `simple`：类似 correlated，并且支持`waitForConfirms()`和 `waitForConfirmsOrDie()`方法的调用

首先，我们需要对消息发送模板进行改造，

```java
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
```

关于这个配置类，我说如下几点

1. 定义配置类，实现 `RabbitTemplate.ConfirmCallback` 和 `RabbitTemplate.ReturnsCallback` 两个接口，这两个接口，前者的回调用来确定消息到达交换器，后者则会在消息路由到队列失败时被调用
2. 定义`initRabbitTemplate`方法并添加`@PostConstruct`注解，在该方法中为`rabbitTemplate`分别配置这两个Callback

然后我们发送消息给一个不存在的交换机，应该会触发消息到达交换机的回调，记得带上CorrelationId，这个方法的前两个参数分别需要填上交换机名称和队列名称，但我现在是乱填的。

```java
@Test
	void contextLoads() {
			    rabbitTemplate.convertAndSend("aaa","bbb","一条消息",new CorrelationData("99"));
	}
```

会得到消息，ack为false，说找不到交换机，并且通过correlationId可以确定是我们发的那条消息

```
消息到达交换机：ack:false;cause:channel error; protocol method: #method<channel.close>(reply-code=404, reply-text=NOT_FOUND - no exchange 'aaa' in vhost '/', class-id=60, method-id=40);correlationId:99;
```

如果我们正确发送交换机，但是写错了代码，就会触发消息未正确到达队列的回调

```java
@Test
	void contextLoads() {
		rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_NAME,"bbb","一条消息",new CorrelationData("99"));
	}
```

得到消息

```
2023-07-16 17:48:18.543  INFO 1380 --- [nectionFactory1] c.h.produce02.config.RabbitConfig        : 消息未到达消息队列：message:一条消息;exchange:exchange_01;queue:bbb
2023-07-16 17:48:18.546  INFO 1380 --- [nectionFactory2] c.h.produce02.config.RabbitConfig        : 消息到达交换机：ack:true;cause:null;correlationId:99;
```

不过这种情况下，肯定是自己写错了代码，再怎么重试也没用，只有第一种对我们有用，我们确认消息发送给了rabbitMQ就行了，如果这一步发送失败，我们再根据情况进行重试操作。

##### 批量消息处理

如果是消息批量处理，那么发送成功的回调监听是一样的，这里不再赘述。

这就是 publisher-confirm 模式。

相比于事务，这种模式下的消息吞吐量会得到极大的提升。

#### 重试机制

失败重试分两种情况，一种是压根没找到 MQ 导致的失败重试，另一种是找到 MQ 了，但是消息发送失败了。 两种重试我们分别来看。

##### 自带重试机制

前面所说的事务机制和发送方确认机制，都是发送方确认消息发送成功的办法。如果发送方一开始就连 不上 MQ，那么 Spring Boot 中也有相应的重试机制，但是这个重试机制就和 MQ 本身没有关系了，这 是利用 Spring 中的 retry 机制来完成的，具体配置如下：

```properties
#消息失败重试
spring.rabbitmq.template.retry.enabled=true
spring.rabbitmq.template.retry.initial-interval=1000ms
spring.rabbitmq.template.retry.max-attempts=10
spring.rabbitmq.template.retry.max-interval=10000ms
spring.rabbitmq.template.retry.multiplier=2
```

从上往下配置含义依次是：

- 开启重试机制
- 重试起始间隔时间
- 最大重试次数
-  最大重试间隔时间
- 间隔时间乘数（这里配置间隔时间乘数为 2，则第一次间隔时间 1 秒，第二次重试间隔时间 2 秒，第三次 4 秒，以此类推）

配置完成后，再次启动 Spring Boot 项目，然后关掉 MQ，此时尝试发送消息，就会发送失败，进而导致自动重试。

##### 业务重试

业务重试主要是针对消息没有到达交换器的情况。

如果消息没有成功到达交换器，根据我们之前的讲解，此时就会触发消息发送失败回调，在这个回调中，我们就可以做文章了

整体思路是这样

1. 首先创建一张表，用来记录发送到中间件上的消息，像下面这样：

   ![image-20230716183505229](MQ实操笔记.assets/image-20230716183505229.png)

   每次发送消息的时候，就往数据库中添加一条记录。这里的字段都很好理解，有三个我额外说下：

   - status：表示消息的状态，有三个取值，0，1，2 分别表示消息发送中、消息发送成功以及消息发送失败。
   - tryTime：表示消息的第一次重试时间（消息发出去之后，在 tryTime 这个时间点还未显示发送成功，此时就可以开始重试了）。
   - count：表示消息重试次数。

2. 在消息发送的时候，我们就往该表中保存一条消息发送记录，并设置状态 status 为 0，tryTime 为 1 分钟之后。

3. 在 confirm 回调方法中，如果收到消息发送成功的回调，就将该条消息的 status 设置为1（在消息发送时为消息设置 msgId，在消息发送成功回调时，通过 msgId 来唯一锁定该条消息）。

4.  另外开启一个定时任务，定时任务每隔 10s 就去数据库中捞一次消息，专门去捞那些 status 为 0 并且已经过了 tryTime 时间记录，把这些消息拎出来后，首先判断其重试次数是否已超过 3 次， 如果超过 3 次，则修改该条消息的 status 为 2，表示这条消息发送失败，并且不再重试。对于重试次数没有超过 3 次的记录，则重新去发送消息，并且为其 count 的值+1。

当然这种思路有两个弊端：

1. 去数据库走一遭，可能拖慢 MQ 的 Qos，不过有的时候我们并不需要 MQ 有很高的 Qos，所以这 个应用时要看具体情况
2. 按照上面的思路，可能会出现同一条消息重复发送的情况，不过这都不是事，我们在消息消费时， 解决好幂等性问题就行了。 当然，大家也要注意，消息是否要确保 100% 发送成功，也要看具体情况。

### 确保消息消费

新的问题，消息发送成功了，怎么确保它一定被消费了呢？

要解决这个问题，我们先了解一下消息是如何消费的

- 推（push）：MQ 主动将消息推送给消费者，这种方式需要消费者设置一个缓冲区去缓存消息， 对于消费者而言，内存中总是有一堆需要处理的消息，所以这种方式的效率比较高，这也是目前大多数应用采用的消费方式。
- 拉（pull）：消费者主动从 MQ 拉取消息，这种方式效率并不是很高，不过有的时候如果服务端需要批量拉取消息，倒是可以采用这种方式。

我们不做任何设置，默认就是推的模式，只要监听的队列中有消息，就会自动去消费。

拉取：

```java
rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME,"你好恒星！");
String o = (String) rabbitTemplate.receiveAndConvert(RabbitMQConfig.QUEUE_NAME);
System.out.println("消息：" + o);
```

调用 receiveAndConvert 方法，方法参数为队列名称，方法执行完成后，会从 MQ 上拉取一条消息下 来，如果该方法返回值为 null，表示该队列上没有消息了。receiveAndConvert 方法有一个重载方法， 可以在重载方法中传入一个等待超时时间，例如 3 秒。此时，假设队列中没有消息了，则 receiveAndConvert 方法会阻塞 3 秒，3 秒内如果队列中有了新消息就返回，3 秒后如果队列中还是没有新消息，就返回 null，这个等待超时时间要是不设置的话，默认为 0。

> 如果需要从消息队列中持续获得消息，就可以使用推模式；如果只是单纯的消费一条消息，则使用拉模式即可。==切忌将拉模式放到一个死循环中，变相的订阅消息，这会严重影响 RabbitMQ 的性能。==

#### 确保消费成功两种思路

为了保证消息能够可靠的到达消息消费者，RabbitMQ 中提供了消息消费确认机制。当消费者去消费消息的时候，可以通过指定 autoAck 参数来表示消息消费的确认方式

- 当 autoAck 为 false 的时候，此时即使消费者已经收到消息了，RabbitMQ 也不会立马将消息移除，而是等待消费者显式的回复确认信号后，才会将消息打上删除标记，然后再删除
- 当 autoAck 为 true 的时候，消息一旦被接收，消费者自动发送 ACK

此图是rabbit MQ的管理页面

![管理页面](https://s2.loli.net/2023/07/16/2xIytYT53OzbRoA.png)

- Ready 表示待消费的消息数量
- Unacked 表示已经发送给消费者但是还没收到消费者 ack 的消息数量。

那么在autoAck为false时，对于 RabbitMQ 而言，消费分成了两个部分：

- 待消费的消息
- 已经投递给消费者，但是还没有被消费者确认的消息

只要我消费者在处理完业务之后，再告诉去手动ack，RabbitMQ 才会认为消息消费成功，否则如果没有收到确认，就会重新放回队列，等待下一次消费。

所以，要确保消息成功消费，无非是==手动还是自动ack==，没有其他路子。

但是也要注意，这两种方案都可能导致重复消费

##### 消息确认

首先是自动确认

其实就是正常的消费者，默认就是这个情况。

消息自带事务，消息内部产生异常会自动回滚，然后重新消费。

```java
@RabbitListener(queues = RabbitConfig.QUEUE_NAME)
public void handle(String message){
    LOGGER.info("message:" + message);
    int i = 1 / 0;
}
```

其次是手动确认

首先在设置中调整为手动模式

```properties
spring.rabbitmq.listener.simple.acknowledge-mode=manual
```

消费者里，在业务执行完后，再手动ack

```java
@Component
public class Consumer01 {

    private static final Logger LOGGER = LoggerFactory.getLogger(Consumer01.class);

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
                // channel.basicReject(deliveryTag,false);
            } catch (IOException ex){
                ex.printStackTrace();
            }
        }
    }
}
```

将消费者要做的事情放到一个`try...catch`代码块中。

如果消息正常消费成功，则执行`basicAck`完成确认

如果消息消费失败，则执行`basicNack`方法，告诉 RabbitMQ 消息消费失败。

这里涉及到两个方法：

- `basicAck`：这个是手动确认消息已经成功消费，该方法有两个参数：第一个参数表示消息的 id； 第二个参数 multiple 如果为 false，表示仅确认当前消息消费成功，如果为 true，则表示当前消息 之前所有未被当前消费者确认的消息都消费成功。
- `basicNack`：这个是告诉 RabbitMQ 当前消息未被成功消费，该方法有三个参数：
   - 第一个参数表示 消息的 id
   - 第二个参数 multiple 如果为 false，表示仅拒绝当前消息的消费，如果为 true，则表示拒绝==当前消息之前所有==未被当前消费者确认的消息
   - 第三个参数 requeue 含义和前面所说的一 样，被拒绝的消息是否重新入队。





##### 消息拒绝