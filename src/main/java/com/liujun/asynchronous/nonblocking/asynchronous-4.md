# java实现异步非阻塞的几种方式-消息中间件

## 4. 消息中间件

在前面已经实现了异步调用、接口回调两种形式，都较为复杂，需要自己控制多线程，那有没有简单点的方式来实现异步的消息通讯。这个当然是有的，可以使用消息中间件的形式，来实现整个消息的异步调用。还是以订单这个场景为示例来举个粟子。

![](D:\doc\博客\总续实践\非阻塞式调用\消息中间件\消息中间件调用序列图.png)



### 4.1 样例代码

我的样例程序使用rabbitmq来做为消息队列传输数据，其他消息队列功能类似。

先从订单服务开始。

```java
@RestController
@RequestMapping("/order")
public class OrderServerFacade {

  private Logger logger = LoggerFactory.getLogger(OrderServerFacade.class);

  /** 消息的发送接口 */
  @Autowired private RabbitTemplate orderRabbitTemplate;

  /** 事件处理 */
  @Autowired private OrderEventProcess eventProcess;

  @RequestMapping(
      value = "/orderInfo",
      method = {RequestMethod.POST})
  public ApiResponse getUserInfo(@RequestBody OrderDTO order) {
    logger.info("getUserInfo start {}", order);
    // 保存当前的发送数据
    OrderEventProcess.putOrder(order);
    // 获取用户信息的请求发送
    boolean userSender = this.senderQueryUser(order.getUserId());
    // 获取商品信息的请求发送
    boolean goodsSender = this.senderQueryGoods(order.getGoodId());
    logger.info("getUserInfo  request {} rsponse {} ", order.getUserId(), userSender);
    logger.info("getGoods  request {} rsponse {} ", order.getGoodId(), goodsSender);

    // 构建结果的响应
    return ApiResponse.ok();
  }

  @RequestMapping(
      value = "/getOrderResponse",
      method = {RequestMethod.POST})
  public ApiResponse getOrderInfo(@RequestBody OrderDTO order) {
    logger.info("getOrderResponse start {}", order);
    // 获取用户信息的请求发送
    OrderDTO userQueryRsp = eventProcess.builderRsp(order);
    logger.info("getOrderResponse  request {} rsponse {} ", order.getUserId(), userQueryRsp);

    // 构建结果的响应
    return ApiResponse.ok(userQueryRsp);
  }

  /**
   * 获取用户的信息
   *
   * @param userId 用户的id
   * @return 用户的信息
   */
  private boolean senderQueryUser(String userId) {
    logger.info("request get user info start {} ", userId);
    ClientUserDTO clientUser = new ClientUserDTO();
    clientUser.setUserId(userId);

    // 使用 convertAndSend 方法时的结果：输出时没有顺序，不需要等待，直接运行
    orderRabbitTemplate.convertAndSend(
        RabbitmqConfig.getUserReqExchangeName(),
        RabbitmqConfig.getUserReqQueueName(),
        // 数据的格式为json
        JsonUtils.toJson(clientUser),
        msg -> {
          msg.getMessageProperties()
              .getHeaders()
              .put(RabbitmqConfig.getHeadType(), DataTypeEnum.USER.getType());
          return msg;
        });

    return true;
  }

  /**
   * 获取商品信息
   *
   * @param dataId 商品信息
   * @return 当前的用户的信息
   */
  private boolean senderQueryGoods(String dataId) {
    logger.info("request goods start {} ", dataId);
    ClientGoodsDTO clientGoods = new ClientGoodsDTO();
    clientGoods.setDataId(dataId);

    // 使用 convertAndSend 方法时的结果：输出时没有顺序，不需要等待，直接运行
    orderRabbitTemplate.convertAndSend(
        RabbitmqConfig.getGoodsReqExchangeName(),
        RabbitmqConfig.getGoodsReqQueueName(),
        // 数据的格式为json
        JsonUtils.toJson(clientGoods),
        msg -> {
          msg.getMessageProperties()
              .getHeaders()
              .put(RabbitmqConfig.getHeadType(), DataTypeEnum.GOODS.getType());
          return msg;
        });

    return true;
  }
}
```

这是订单的服务。它的作用就是向用户服务和端口服务以消息队列的形式发送数据，消息队列具备可保存数据的功能，所以仅将消息发送至消息中间件即可。然后还需要再启动一个监听接收来自消息队列的数据。

```java
@Service("orderEventProcess")
public class OrderEventProcess implements ChannelAwareMessageListener {

  /** 日志 */
  private Logger logger = LoggerFactory.getLogger(OrderEventProcess.class);

  /** 数据类型的标识信息 */
  public static final String DATA_TYPE = "DATA_TYPE";

  /** 用来用户的map */
  private static final Map<String, OrderDTO> USER_MAP = new ConcurrentHashMap<>();

  /** 用户存储商品的map */
  private static final Map<String, OrderDTO> GOODS_MAP = new ConcurrentHashMap<>();

  @Override
  public void onMessage(Message message, Channel channel) throws Exception {
    // 通过类型确定消息的处理类
    String dataType = String.valueOf(message.getMessageProperties().getHeaders().get(DATA_TYPE));
    // json串信息
    String msgInfo = new String(message.getBody());
    // 回调保存用户
    if (DataTypeEnum.USER.getType().equals(dataType)) {
      this.saveUser(msgInfo);
    }
    // 保存商品信息
    else if (DataTypeEnum.GOODS.getType().equals(dataType)) {
      this.saveGoods(msgInfo);
    }
  }

  /**
   * 构造响应信息
   *
   * @param orderInfo 用户查询请求信息
   * @return 响应结果
   */
  public OrderDTO builderRsp(OrderDTO orderInfo) {
    return USER_MAP.get(orderInfo.getUserId());
  }

  /**
   * 保存用户信息
   *
   * @param data
   */
  private void saveUser(String data) {
    ClientUserDTO userRsp = JsonUtils.fromJson(data, new TypeToken<ClientUserDTO>() {}.getType());
    OrderDTO oderInfo = USER_MAP.get(userRsp.getUserId());
    if (oderInfo != null) {
      logger.info("save user response {} ",userRsp);
      oderInfo.setUserInfo(userRsp);
    }
  }

  /**
   * 保存商品信息
   *
   * @param data
   */
  private void saveGoods(String data) {
    ClientGoodsDTO goodsRsp =
        JsonUtils.fromJson(data, new TypeToken<ClientGoodsDTO>() {}.getType());
    OrderDTO orderInfo = GOODS_MAP.get(goodsRsp.getDataId());
    if (orderInfo != null) {
      logger.info("save goods response : {}",goodsRsp);
      orderInfo.setGoodsInfo(goodsRsp);
    }
  }

  /**
   * 执行消息的存储操作
   *
   * @param orderInfo
   */
  public static void putOrder(OrderDTO orderInfo) {
    USER_MAP.put(orderInfo.getUserId(), orderInfo);
    GOODS_MAP.put(orderInfo.getGoodId(), orderInfo);
  }
}
```



看完成订单的发送。再来看用户服务:

用户服务接收来自消息队列的数据。交给UserEventProcess处理.

```java
@Service("userEventProcess")
public class UserEventProcess implements ChannelAwareMessageListener {

  /** 日志 */
  private Logger logger = LoggerFactory.getLogger(UserEventProcess.class);

  /** 数据类型的标识信息 */
  public static final String DATA_TYPE = "DATA_TYPE";

  /** 消息的发送接口 */
  @Autowired private RabbitTemplate amqpTemplate;

  @Override
  public void onMessage(Message message, Channel channel) throws Exception {

    // 通过类型确定消息的处理类
    String dataType = String.valueOf(message.getMessageProperties().getHeaders().get(DATA_TYPE));

    // json串信息
    String msgInfo = new String(message.getBody());
    // 当数据接收完毕后，则进行回复消息
    if (DataTypeEnum.USER.getType().equals(dataType)) {
      UserDTO userData = this.receiveData(msgInfo);
      ThreadUtils.sleep(5);

      logger.info("user event process  request {} response {}",msgInfo,userData);

      // 用户消息的发送
      this.sendData(userData, DataTypeEnum.USER.getType());
    }
  }

  /**
   * 保存用户信息
   *
   * @param data
   */
  private UserDTO receiveData(String data) {
    UserDTO userRsp = JsonUtils.fromJson(data, new TypeToken<UserDTO>() {}.getType());
    UserDTO rsp = new UserDTO();
    rsp.setUserId(userRsp.getUserId());
    rsp.setName("bug_null");
    rsp.setAddress("this is shanghai");

    return rsp;
  }

  /**
   * 使用 convertAndSend 方法时的结果：输出时没有顺序，不需要等待，直接运行,仅测试使用，
   *
   * <p>线上环境一般需要使用确认机制。
   *
   * @param message 发送的消息
   * @param dataType 数据类型信息
   */
  private void sendData(UserDTO message, String dataType) {
    // 使用 convertAndSend 方法时的结果：输出时没有顺序，不需要等待，直接运行
    amqpTemplate.convertAndSend(
        RabbitmqConfig.getRspExchangeName(),
        RabbitmqConfig.getRspQueueName(),
        // 数据的格式为json
        JsonUtils.toJson(message),
        msg -> {
          msg.getMessageProperties().getHeaders().put(RabbitmqConfig.getHeadType(), dataType);
          return msg;
        });
  }
}
```

消息队列处理完成后，将数据再通过消息队列发送给订单服务。



最后再来看下商品服务

```java
@Service("goodsEventProcess")
public class GoodsEventProcess implements ChannelAwareMessageListener {

  /** 日志服务 */
  private Logger logger = LoggerFactory.getLogger(GoodsEventProcess.class);

  /** 数据类型的标识信息 */
  public static final String DATA_TYPE = "DATA_TYPE";

  /** 消息的发送接口 */
  @Autowired private RabbitTemplate amqpTemplate;

  @Override
  public void onMessage(Message message, Channel channel) throws Exception {
    // 通过类型确定消息的处理类
    String dataType = String.valueOf(message.getMessageProperties().getHeaders().get(DATA_TYPE));
    // json串信息
    String msgInfo = new String(message.getBody());
    // 当数据接收完毕后，则进行回复消息
    if (DataTypeEnum.GOODS.getType().equals(dataType)) {
      GoodsDTO userData = this.receiveData(msgInfo);

      ThreadUtils.sleep(10);

      logger.info("goodsEventProcess onMessage receive : {} response : {} ", msgInfo, userData);
      // 用户消息的发送
      this.sendData(userData, DataTypeEnum.GOODS.getType());
    }
  }

  /**
   * 保存用户信息
   *
   * @param data
   */
  private GoodsDTO receiveData(String data) {
    GoodsDTO goods = JsonUtils.fromJson(data, new TypeToken<GoodsDTO>() {}.getType());
    GoodsDTO rsp = new GoodsDTO();
    rsp.setDataId(goods.getDataId());
    goods.setGoodsPrice(1024);
    goods.setMessage("这是一个苹果,apple,还被咬了一口");
    return goods;
  }

  /**
   * 使用 convertAndSend 方法时的结果：输出时没有顺序，不需要等待，直接运行,仅测试使用，
   *
   * <p>线上环境一般需要使用确认机制。
   *
   * @param message 发送的消息
   * @param dataType 数据类型信息
   */
  private void sendData(GoodsDTO message, String dataType) {
    // 使用 convertAndSend 方法时的结果：输出时没有顺序，不需要等待，直接运行
    amqpTemplate.convertAndSend(
        RabbitmqConfig.getRspExchangeName(),
        RabbitmqConfig.getRspQueueName(),
        // 数据的格式为json
        JsonUtils.toJson(message),
        msg -> {
          msg.getMessageProperties().getHeaders().put(RabbitmqConfig.getHeadType(), dataType);
          return msg;
        });
  }
}
```

商品服务与用户服务类似，功能都是接收数据，进行处理，再通过消息通道将数据写回至消息队列中



在所有的服务都需要进行消息队列的监听。这部分就放在这里作为一个公共部分:

 消息队列的监听：

```java
@Configuration
public class RabbitmqConfigBean {

  /** 日志信息 */
  private Logger log = LoggerFactory.getLogger(RabbitmqConfigBean.class);

  /** 用户服务的监听 */
  @Autowired private OrderEventProcess orderEventProcess;

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
  @Bean("orderSimpleMessageListenerContainer")
  public SimpleMessageListenerContainer simpleMessageListenerContainer(
      @Qualifier("orderRabbitMQConnectionFactory") ConnectionFactory connectionFactory) {
    SimpleMessageListenerContainer container =
        new SimpleMessageListenerContainer(connectionFactory);
    container.setConcurrentConsumers(1);
    container.setMaxConcurrentConsumers(1);
    // RabbitMQ默认是自动确认，这里改为手动确认消息
    container.setAcknowledgeMode(AcknowledgeMode.AUTO);
    // 设置一个队列
    container.setQueueNames(RabbitmqConfig.getRspQueueName());
    container.setMessageListener(orderEventProcess);

    return container;
  }

  /**
   * 构建mq的连接工厂信息
   *
   * @return
   */
  @Bean(name = "orderRabbitMQConnectionFactory")
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
  @Bean(name = "orderFactory")
  public SimpleRabbitListenerContainerFactory rabbitListenerFactory(
      SimpleRabbitListenerContainerFactoryConfigurer configurer,
      @Qualifier("orderRabbitMQConnectionFactory") ConnectionFactory connectionFactory) {
    SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
    configurer.configure(factory, connectionFactory);

    return factory;
  }

  /**
   * 构建直连型交换机信息
   *
   * @return 交换机实例,
   */
  @Bean(name = "orderExchange")
  public DirectExchange orderDirectExchange() {
    return new DirectExchange(RabbitmqConfig.getRspExchangeName(), true, false);
  }

  /**
   * 资源数据队列信息
   *
   * @return 队列信息
   */
  @Bean(name = "orderQueue")
  public Queue orderQueue() {
    // 第一个参数是队列名字， 第二个参数是指是否持久化
    return new Queue(RabbitmqConfig.getRspQueueName(), true);
  }

  /**
   * 进行交换机与队列的绑定操作
   *
   * @return
   */
  @Bean(name = "orderBind")
  public Binding orderBind() {
    return BindingBuilder.bind(orderQueue())
        .to(orderDirectExchange())
        .with(RabbitmqConfig.getRspQueueName());
  }

  /**
   * 构建资源的mq的模板信息
   *
   * @param connectionFactory 连接工厂
   * @return
   */
  @Bean(name = "orderRabbitTemplate")
  @Primary
  public RabbitTemplate resourceRabbitTemplate(
      @Qualifier("orderRabbitMQConnectionFactory") ConnectionFactory connectionFactory) {
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
```

### 4.2 单元测试

```java
public class TestOrderServerFacade {

  /** json转换对象 */
  private Gson gson = new Gson();

  /** 发送对象 */
  protected RestTemplate restTemplate = RestTemplateUtils.INSTANCE.getRestTemplate();

  @Test
  public void testOrder() {
    sendRequest();
    // 每1秒获取一次结果
    for (int i = 0; i < 11; i++) {
      checkResponse(i);
      System.out.println();
      ThreadUtils.sleep(1);
    }
  }

  private void sendRequest() {
    OrderDTO orderInfo = new OrderDTO();
    orderInfo.setGoodId("2001");
    orderInfo.setUserId("1001");
    // 将对象装入HttpEntity中
    HttpEntity<OrderDTO> request = new HttpEntity<>(orderInfo);
    ResponseEntity<String> result =
        restTemplate.postForEntity("http://localhost:9010/order/orderInfo", request, String.class);
    Assert.assertEquals(HttpStatus.OK.value(), result.getStatusCodeValue());
    ApiResponse data = gson.fromJson(result.getBody(), new TypeToken<ApiResponse>() {}.getType());
    Assert.assertEquals(data.getResult(), Boolean.TRUE);
    Assert.assertEquals(data.getCode(), APICodeEnum.SUCCESS.getErrorData().getCode());
  }

  private void checkResponse(int index) {
    OrderDTO orderInfo = new OrderDTO();
    orderInfo.setGoodId("2001");
    orderInfo.setUserId("1001");
    // 将对象装入HttpEntity中
    HttpEntity<OrderDTO> request = new HttpEntity<>(orderInfo);
    ResponseEntity<String> result =
        restTemplate.postForEntity(
            "http://localhost:9010/order/getResponse", request, String.class);
    Assert.assertEquals(HttpStatus.OK.value(), result.getStatusCodeValue());
    ApiResponse<OrderDTO> data =
        gson.fromJson(result.getBody(), new TypeToken<ApiResponse<OrderDTO>>() {}.getType());
    System.out.println(data);
    Assert.assertEquals(data.getResult(), Boolean.TRUE);
    Assert.assertEquals(data.getCode(), APICodeEnum.SUCCESS.getErrorData().getCode());
    if (index > 5) {
      Assert.assertNotNull(data.getData().getUserInfo());
    }
    if (index > 10) {
      Assert.assertNotNull(data.getData().getGoodsInfo());
    }
  }
}
```







### 4.3 分析：

再来看下控制台结果：

client(junit):

```
22:23:01.655 [main] DEBUG org.springframework.web.client.RestTemplate - HTTP POST http://localhost:9010/order/getResponse
22:23:01.655 [main] DEBUG org.springframework.web.client.RestTemplate - Accept=[text/plain, application/json, application/*+json, */*]
22:23:01.655 [main] DEBUG org.springframework.web.client.RestTemplate - Writing [OrderDTO{userId='1001', userInfo=null, goodId='2001', goodsInfo=null}] with org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
22:23:01.671 [main] DEBUG org.springframework.web.client.RestTemplate - Response 200 OK
22:23:01.671 [main] DEBUG org.springframework.web.client.RestTemplate - Reading to [java.lang.String] as "application/json;charset=UTF-8"
ApiResponse{result=true, code=0, msg='操作成功', data=OrderDTO{userId='1001', userInfo=null, goodId='2001', goodsInfo=null}, count=null}

.......

22:23:06.765 [main] DEBUG org.springframework.web.client.RestTemplate - HTTP POST http://localhost:9010/order/getResponse
22:23:06.765 [main] DEBUG org.springframework.web.client.RestTemplate - Accept=[text/plain, application/json, application/*+json, */*]
22:23:06.765 [main] DEBUG org.springframework.web.client.RestTemplate - Writing [OrderDTO{userId='1001', userInfo=null, goodId='2001', goodsInfo=null}] with org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
22:23:06.781 [main] DEBUG org.springframework.web.client.RestTemplate - Response 200 OK
22:23:06.781 [main] DEBUG org.springframework.web.client.RestTemplate - Reading to [java.lang.String] as "application/json;charset=UTF-8"
ApiResponse{result=true, code=0, msg='操作成功', data=OrderDTO{userId='1001', userInfo=UserDTO{userId='1001', name='bug_null', address='this is shanghai'}, goodId='2001', goodsInfo=null}, count=null}

.......

22:23:11.860 [main] DEBUG org.springframework.web.client.RestTemplate - HTTP POST http://localhost:9010/order/getResponse
22:23:11.860 [main] DEBUG org.springframework.web.client.RestTemplate - Accept=[text/plain, application/json, application/*+json, */*]
22:23:11.860 [main] DEBUG org.springframework.web.client.RestTemplate - Writing [OrderDTO{userId='1001', userInfo=null, goodId='2001', goodsInfo=null}] with org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
22:23:11.875 [main] DEBUG org.springframework.web.client.RestTemplate - Response 200 OK
22:23:11.875 [main] DEBUG org.springframework.web.client.RestTemplate - Reading to [java.lang.String] as "application/json;charset=UTF-8"
ApiResponse{result=true, code=0, msg='操作成功', data=OrderDTO{userId='1001', userInfo=UserDTO{userId='1001', name='bug_null', address='this is shanghai'}, goodId='2001', goodsInfo=Goods{dataId='2001', goodsPrice=1024, message='这是一个苹果,apple,还被咬了一口'}}, count=null}

```



order:

```
2021-03-16 22:23:01.624  INFO 5128 --- [nio-9010-exec-4] c.l.a.n.i.e.order.OrderServerFacade      : getUserInfo start OrderDTO{userId='1001', userInfo=null, goodId='2001', goodsInfo=null}
2021-03-16 22:23:01.624  INFO 5128 --- [nio-9010-exec-4] c.l.a.n.i.e.order.OrderServerFacade      : request get user info start 1001 
2021-03-16 22:23:01.624  INFO 5128 --- [nio-9010-exec-4] c.l.a.n.i.e.order.OrderServerFacade      : request goods start 2001 
2021-03-16 22:23:01.624  INFO 5128 --- [nio-9010-exec-4] c.l.a.n.i.e.order.OrderServerFacade      : getUserInfo  request 1001 rsponse true 
2021-03-16 22:23:01.624  INFO 5128 --- [nio-9010-exec-4] c.l.a.n.i.e.order.OrderServerFacade      : getGoods  request 2001 rsponse true 
2021-03-16 22:23:01.655  INFO 5128 --- [ 127.0.0.1:5672] c.l.a.n.i.e.order.RabbitmqConfigBean     : ConfirmCallBackListener config :correlationData:null,ack:true ,cause: null
2021-03-16 22:23:01.655  INFO 5128 --- [ 127.0.0.1:5672] c.l.a.n.i.e.order.RabbitmqConfigBean     : ConfirmCallBackListener config :correlationData:null,ack:true ,cause: null
.......
2021-03-16 22:23:05.765  INFO 5128 --- [nio-9010-exec-8] c.l.a.n.i.e.order.OrderServerFacade      : getOrderResponse  request 1001 rsponse OrderDTO{userId='1001', userInfo=null, goodId='2001', goodsInfo=null} 
2021-03-16 22:23:06.640  INFO 5128 --- [enerContainer-1] c.l.a.n.i.e.order.OrderEventProcess      : save user response UserDTO{userId='1001', name='bug_null', address='this is shanghai'} 
2021-03-16 22:23:06.781  INFO 5128 --- [io-9010-exec-10] c.l.a.n.i.e.order.OrderServerFacade      : getOrderResponse start OrderDTO{userId='1001', userInfo=null, goodId='2001', goodsInfo=null}
2021-03-16 22:23:06.781  INFO 5128 --- [io-9010-exec-10] c.l.a.n.i.e.order.OrderServerFacade      : getOrderResponse  request 1001 rsponse OrderDTO{userId='1001', userInfo=UserDTO{userId='1001', name='bug_null', address='this is shanghai'}, goodId='2001', goodsInfo=null} 

......

2021-03-16 22:23:10.860  INFO 5128 --- [nio-9010-exec-4] c.l.a.n.i.e.order.OrderServerFacade      : getOrderResponse  request 1001 rsponse OrderDTO{userId='1001', userInfo=UserDTO{userId='1001', name='bug_null', address='this is shanghai'}, goodId='2001', goodsInfo=null} 
2021-03-16 22:23:11.625  INFO 5128 --- [enerContainer-1] c.l.a.n.i.e.order.OrderEventProcess      : save goods response : Goods{dataId='2001', goodsPrice=1024, message='这是一个苹果,apple,还被咬了一口'}
2021-03-16 22:23:11.875  INFO 5128 --- [nio-9010-exec-5] c.l.a.n.i.e.order.OrderServerFacade      : getOrderResponse start OrderDTO{userId='1001', userInfo=null, goodId='2001', goodsInfo=null}
2021-03-16 22:23:11.875  INFO 5128 --- [nio-9010-exec-5] c.l.a.n.i.e.order.OrderServerFacade      : getOrderResponse  request 1001 rsponse OrderDTO{userId='1001', userInfo=UserDTO{userId='1001', name='bug_null', address='this is shanghai'}, goodId='2001', goodsInfo=Goods{dataId='2001', goodsPrice=1024, message='这是一个苹果,apple,还被咬了一口'}} 
```

user:

```
2021-03-16 22:23:06.640  INFO 8872 --- [enerContainer-1] c.l.a.n.i.e.user.UserEventProcess        : user event process  request {"userId":"1001"} response UserDTO{userId='1001', name='bug_null', address='this is shanghai'}
2021-03-16 22:23:06.640  INFO 8872 --- [ 127.0.0.1:5672] c.l.a.n.i.e.user.RabbitmqConfigBean      : ConfirmCallBackListener config :correlationData:null,ack:true ,cause: null
```



goods:

```
2021-03-16 22:23:11.625  INFO 4620 --- [enerContainer-1] c.l.a.n.i.e.goods.GoodsEventProcess      : goodsEventProcess onMessage receive : {"dataId":"2001","goodsPrice":0} response : GoodsDTO{dataId='2001', goodsPrice=1024, message='这是一个苹果,apple,还被咬了一口'} 
2021-03-16 22:23:11.625  INFO 4620 --- [ 127.0.0.1:5672] c.l.a.n.i.e.goods.RabbitmqConfigBean     : ConfirmCallBackListener config :correlationData:null,ack:true ,cause: null

```

通过观察日志可以发现。在2021-03-16 22:23:01.624时，order将消息发送至消息队列。user在2021-03-16 22:23:06.640时处理完，并进行了写回 队列操作,order在2021-03-16 22:23:06.781也收到了回复，并进行了存储，goods在2021-03-16 22:23:11.625时处理完成， 也进行了写回,order在2021-03-16 22:23:11.875收到响应，并进行了存储。



这里再来看下完整的线程的模型：

![](D:\doc\博客\总续实践\非阻塞式调用\消息中间件\中间件线程模型.png)

### 4.4 总结：

本章使用线程队列实现一个异步非阻塞的调用。

优势：

使用消息中间件后，服务的双方都不用再等待。

客户端将消息交给消息中间件，服务端从消息中间件接收消息。两边都不再相互依赖，做到了解耦。

消息中间同时可以做数据的存储，可做到消息不丢。

使用消息中间件后。服务的双方都可以按照一个固定的速度来运行任务，做到削峰。

劣势：

由于增加一个组件，对于组件的维护是需要额外的成本的。

在极端情况下，消息还是可能会发生丢失的，如果要做到消息不丢，这就需要使用类似可靠消息事件模式等手段来保证一致性。

由于发送与接收的线程分离，针对查询类的一些场景，需要服务端做一些数据的缓存。





详细代码请参看我的github:

https://github.com/kkzfl22/demojava8/blob/master/src/main/java/com/liujun/asynchronous/nonblocking/invoke/eventbus/order/OrderServerFacade.java



