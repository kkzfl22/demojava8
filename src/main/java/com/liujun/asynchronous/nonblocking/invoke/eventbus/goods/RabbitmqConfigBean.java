package com.liujun.asynchronous.nonblocking.invoke.eventbus.goods;

import com.liujun.asynchronous.nonblocking.invoke.eventbus.constants.RabbitmqConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 消息中间件的连接配制
 *
 * @author liujun
 * @version 0.0.1
 */
@Configuration
public class RabbitmqConfigBean {

  /** 日志信息 */
  private Logger log = LoggerFactory.getLogger(RabbitmqConfigBean.class);

  /** 用户服务的监听 */
  @Autowired private GoodsEventProcess goodsEventProcess;

  /**
   * 注册相关的消息监控器
   *
   * <p>另一种设置队列的方法,如果使用这种情况,那么要设置多个,就使用addQueues
   *
   * <p>container.setQueues(new Queue("TestDirectQueue",true));
   *
   * <p>container.addQueues(new Queue("TestDirectQueue2",true));
   *
   * @param connectionFactory
   * @return
   */
  @Bean("goodsSimpleMessageListenerContainer")
  public SimpleMessageListenerContainer simpleMessageListenerContainer(
      @Qualifier("rabbitMQConnectionFactory") ConnectionFactory connectionFactory) {
    SimpleMessageListenerContainer container =
        new SimpleMessageListenerContainer(connectionFactory);
    container.setConcurrentConsumers(1);
    container.setMaxConcurrentConsumers(1);
    // RabbitMQ默认是自动确认，这里改为手动确认消息
    container.setAcknowledgeMode(AcknowledgeMode.AUTO);
    // 设置一个队列
    container.setQueueNames(RabbitmqConfig.getGoodsReqQueueName());
    container.setMessageListener(goodsEventProcess);

    return container;
  }

  /**
   * 构建mq的连接工厂信息
   *
   * @return
   */
  @Bean(name = "rabbitMQConnectionFactory")
  @Primary
  public ConnectionFactory resourceConnectionFactory() {
    CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
    connectionFactory.setHost(RabbitmqConfig.getHost());
    connectionFactory.setPort(RabbitmqConfig.getPort());
    connectionFactory.setUsername(RabbitmqConfig.getUserName());
    connectionFactory.setPassword(RabbitmqConfig.getUserPassword());
    connectionFactory.setVirtualHost(RabbitmqConfig.getVirtualHost());
    // 设置当前需要进行发布确认，防止消息丢失
    connectionFactory.setPublisherConfirms(true);
    return connectionFactory;
  }

  /**
   * 进行监听工厂的配制
   *
   * @param configurer 配制对象信息
   * @param connectionFactory 连接工厂
   * @return 监听配制
   */
  @Bean(name = "resourceFactory")
  public SimpleRabbitListenerContainerFactory rabbitListenerFactory(
      SimpleRabbitListenerContainerFactoryConfigurer configurer,
      @Qualifier("rabbitMQConnectionFactory") ConnectionFactory connectionFactory) {
    SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
    configurer.configure(factory, connectionFactory);

    return factory;
  }

  /**
   * 构建直连型交换机信息
   *
   * @return 交换机实例,
   */
  @Bean
  public DirectExchange resourceDirectExchange() {
    return new DirectExchange(RabbitmqConfig.getGoodsReqExchangeName(), true, false);
  }

  /**
   * 资源数据队列信息
   *
   * @return 队列信息
   */
  @Bean
  public Queue resourceQueue() {
    // 第一个参数是队列名字， 第二个参数是指是否持久化
    return new Queue(RabbitmqConfig.getGoodsReqQueueName(), true);
  }

  /**
   * 进行交换机与队列的绑定操作
   *
   * @return
   */
  @Bean
  public Binding resourceBind() {
    return BindingBuilder.bind(resourceQueue())
        .to(resourceDirectExchange())
        .with(RabbitmqConfig.getGoodsReqQueueName());
  }

  /**
   * 构建资源的mq的模板信息
   *
   * @param connectionFactory 连接工厂
   * @return
   */
  @Bean(name = "resourceRabbitTemplate")
  @Primary
  public RabbitTemplate resourceRabbitTemplate(
      @Qualifier("rabbitMQConnectionFactory") ConnectionFactory connectionFactory) {
    RabbitTemplate resourceRabbitTemplate = new RabbitTemplate(connectionFactory);
    // mandatory 为true，确认函数与返回执行函数才有生交
    resourceRabbitTemplate.setMandatory(true);
    resourceRabbitTemplate.setConfirmCallback(new ConfirmCallBackListener());
    resourceRabbitTemplate.setReturnCallback(new ReturnCallBackListener());
    return resourceRabbitTemplate;
  }

  /** 事件确认机制 */
  public class ConfirmCallBackListener implements RabbitTemplate.ConfirmCallback {
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
      log.info(
          "ConfirmCallBackListener config :correlationData:{},ack:{} ,cause: {}",
          correlationData,
          ack,
          cause);
    }
  }

  /** 回调监听 */
  public class ReturnCallBackListener implements RabbitTemplate.ReturnCallback {
    @Override
    public void returnedMessage(Message message, int i, String s, String s1, String s2) {
      log.info(
          "fail-message:{},replyCode:{},replyText:{},exchange:{},routingKey:{}",
          new String(message.getBody()),
          i,
          s,
          s1,
          s2);
    }
  }
}
