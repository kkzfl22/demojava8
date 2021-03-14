# java实现异步非阻塞的几种方式-3

## 3. 接口回调

在异步阻塞的调用过程中线程还是会存在于阻塞中。等待响应的结果，那能不能在发起请求后就不再关心这个逻辑，而执行其他的任务呢？那就要用到callback机制。

还是以订单查询为示例。先来说下整个的调用链吧。

![](D:\doc\博客\总续实践\非阻塞式调用\callback\http-callback机制.png)

当这个异步回调机制加入之后，原来order的等待的结果的响应就不存在，可以做其他的事情去了，等着其他用户和商品服务完成之后，调用即可。





### 3.1 样例代码

订单服务的代码：

```java
@RestController
@RequestMapping("/order")
public class OrderServerFacade {

  private Logger logger = LoggerFactory.getLogger(OrderServerFacade.class);

  /** 用来存储订单的map */
  private Map<String, OrderDTO> userMap = new HashMap<>();

  /** 商品服务 */
  private Map<String, OrderDTO> goodsMap = new HashMap<>();

  /** 获取连接处理对象 */
  private RestTemplate restTemplate = RestTemplateUtils.INSTANCE.getRestTemplate();

  @RequestMapping(
      value = "/orderInfo",
      method = {RequestMethod.POST})
  public ApiResponse getUserInfo(@RequestBody OrderDTO order) {
    logger.info("getUserInfo start {}", order);
    // 获取用户信息的请求发送
    boolean userSender = this.getUserInfo(order.getUserId());
    // 获取商品信息的请求发送
    boolean goodsSender = this.getGoods(order.getGoodId());
    logger.info("getUserInfo  request {} rsponse {} ", order.getUserId(), userSender);
    logger.info("getGoods  request {} rsponse {} ", order.getGoodId(), goodsSender);
    userMap.put(order.getUserId(), order);
    goodsMap.put(order.getGoodId(), order);
    // 构建结果的响应
    return ApiResponse.ok();
  }

  /**
   * 异步的订单的响应
   *
   * @param order 订单信息
   * @return 获取响应信息
   */
  @RequestMapping(
      value = "/getResponse",
      method = {RequestMethod.POST})
  public ApiResponse getResponse(@RequestBody OrderDTO order) {
    logger.info("getResponse start {}", order);
    // 响应结果
    OrderDTO orderRsp = userMap.get(order.getUserId());
    if (null != orderRsp) {
      return ApiResponse.ok(orderRsp);
    }
    // 构建结果的响应
    return ApiResponse.ok();
  }

  /**
   * 用户信息响应的回调接口
   *
   * @param userDto 用户信息
   * @return 响应处理
   */
  @RequestMapping(
      value = "/userCallBack",
      method = {RequestMethod.POST})
  public ApiResponse getUserCallBack(@RequestBody ClientUserDTO userDto) {
    logger.info("/userCallBack  {}", userDto);
    // 设置用户信息
    OrderDTO orderInfo = userMap.get(userDto.getUserId());
    if (null != orderInfo) {
      orderInfo.setUserInfo(userDto);
    }
    return ApiResponse.ok();
  }

  /**
   * 商品服务的回调接口
   *
   * @param goodsInfo 商品信息
   * @return
   */
  @RequestMapping(
      value = "/goodsCallBack",
      method = {RequestMethod.POST})
  public ApiResponse getGoodsCallBack(@RequestBody ClientGoodsDTO goodsInfo) {
    logger.info("/goodscallBack {}", goodsInfo);
    // 设置用户信息
    OrderDTO orderInfo = goodsMap.get(goodsInfo.getDataId());
    if (null != orderInfo) {
      orderInfo.setGoodsInfo(goodsInfo);
    }
    return ApiResponse.ok();
  }

  /**
   * 获取用户的信息
   *
   * @param userId 用户的id
   * @return 用户的信息
   */
  private boolean getUserInfo(String userId) {
    logger.info("request get user info start {} ", userId);
    ClientUserDTO clientUser = new ClientUserDTO();
    clientUser.setUserId(userId);
    HttpHeaders headers = new HttpHeaders();
    // 将对象装入HttpEntity中
    HttpEntity<ClientUserDTO> request = new HttpEntity<>(clientUser, headers);
    ResponseEntity<String> result =
        restTemplate.exchange(
            "http://localhost:9000/user/getUserInfo", HttpMethod.POST, request, String.class);
    if (HttpStatus.OK.value() == result.getStatusCodeValue()) {
      return true;
    }
    return false;
  }

  /**
   * 获取商品信息
   *
   * @param dataId 商品信息
   * @return 当前的用户的信息
   */
  private boolean getGoods(String dataId) {
    logger.info("request goods start {} ", dataId);
    ClientGoodsDTO clientGoods = new ClientGoodsDTO();
    clientGoods.setDataId(dataId);
    HttpHeaders headers = new HttpHeaders();
    // 将对象装入HttpEntity中
    HttpEntity<ClientGoodsDTO> request = new HttpEntity<>(clientGoods, headers);
    ResponseEntity<String> result =
        restTemplate.postForEntity(
            "http://localhost:9001/goods/getGoodsInfo", request, String.class);
    if (HttpStatus.OK.value() == result.getStatusCodeValue()) {
      return true;
    }
    return false;
  }
}
```

订单服务相对于同步阻塞的服务，多提供了两个回调接口，分别是“/userCallBack”和"/goodsCallBack"这两个接口，这两个接口在收到数据后，将数据写入至本地的map中缓存起来，以供结果查询。



用户服务

```java
@RestController
@RequestMapping("/user")
public class UserService {
  /** 日志信息 */
  private Logger logger = LoggerFactory.getLogger(UserService.class);

  /** 获取连接处理对象 */
  private RestTemplate restTemplate = RestTemplateUtils.INSTANCE.getRestTemplate();

  @RequestMapping(
      value = "/getUserInfo",
      method = {RequestMethod.POST})
  public ApiResponse getUserInfo(@RequestBody UserDTO input) {

    // 提交任务，进行异步执行，执行回调操作
    TaskThreadDataPool.INSTANCE.submit(new RunTask(input));

    return ApiResponse.ok();
  }

  /** 异步通知操作 */
  public class RunTask implements Runnable {
    private UserDTO input;

    public RunTask(UserDTO input) {
      this.input = input;
    }

    @Override
    public void run() {
      UserDTO rsp = new UserDTO();
      rsp.setUserId(input.getUserId());
      rsp.setName("bug_null");
      rsp.setAddress("this is shanghai");
      rsp.setUserId(input.getUserId());

      ThreadUtils.sleep(5);

      // 发送异步通知操作
      callBackUsers(rsp);
    }

    /**
     * 获取商品信息
     *
     * @param userInfo 商品信息
     * @return 当前的用户的信息
     */
    private boolean callBackUsers(UserDTO userInfo) {
      logger.info("request user start {} ", userInfo);
      HttpHeaders headers = new HttpHeaders();
      // 将对象装入HttpEntity中
      HttpEntity<UserDTO> request = new HttpEntity<>(userInfo, headers);
      ResponseEntity<String> result =
          restTemplate.postForEntity(
              "http://localhost:9010/order/userCallBack", request, String.class);
      if (HttpStatus.OK.value() == result.getStatusCodeValue()) {
        return true;
      }

      return false;
    }
  }
}
```

用户服务相对于同步调用，将原来直接执行的任务，改为了异步执行的任务。将用户的查询提交到线程池中去执行.当任务执行完毕后，异步发送查询的结果数据。



商品服务

```java
@RestController
@RequestMapping("/goods")
public class GoodsService {

  /** 日志信息 */
  private Logger logger = LoggerFactory.getLogger(GoodsService.class);

  /** 获取连接处理对象 */
  private RestTemplate restTemplate = RestTemplateUtils.INSTANCE.getRestTemplate();

  @RequestMapping(
      value = "/getGoodsInfo",
      method = {RequestMethod.POST})
  public ApiResponse getUserInfo(@RequestBody GoodsDTO input) {

    // 将异步任务提交线程池执行
    TaskThreadDataPool.INSTANCE.submit(new RunTask(input));

    return ApiResponse.ok();
  }

  /** 异步通知操作 */
  public class RunTask implements Runnable {
    private GoodsDTO input;

    public RunTask(GoodsDTO input) {
      this.input = input;
    }

    @Override
    public void run() {
      GoodsDTO goods = new GoodsDTO();
      goods.setDataId(input.getDataId());
      goods.setGoodsPrice(1024);
      goods.setMessage("这是一个苹果,apple,还被咬了一口");

      ThreadUtils.sleep(10);

      // 发送异步通知操作
      callBackGoods(goods);
    }

    /**
     * 商品的回调操作
     *
     * @param goodsInfo 商品信息
     * @return 当前的用户的信息
     */
    private boolean callBackGoods(GoodsDTO goodsInfo) {

      logger.info("request goods start {} ", goodsInfo);

      HttpHeaders headers = new HttpHeaders();
      // 将对象装入HttpEntity中
      HttpEntity<GoodsDTO> request = new HttpEntity<>(goodsInfo, headers);
      ResponseEntity<String> result =
          restTemplate.postForEntity(
              "http://localhost:9010/order/goodsCallBack", request, String.class);
      if (HttpStatus.OK.value() == result.getStatusCodeValue()) {
        return true;
      }

      return false;
    }
  }
}

```

商品服务相对于同步的调用：也改为了异步的执行，将商品的查询请求的任务提交至线程池中执行，当商品查询的任务执行完成后，使用回调接口发送查询的结果。



### 3.2 测试

单元测试

```java
ublic class TestOrderServerFacade {

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

服务商提供了异步获取的能力，直接调用即可获取相关的数据，不用再同步等待获取结果了。

![](D:\doc\博客\总续实践\非阻塞式调用\callback\callback单均由测试.png)





### 3.3 分析

从订单的日志说起吧

```java
2021-03-13 11:34:26.550  INFO 13596 --- [nio-9010-exec-2] c.l.a.n.i.c.order.OrderServerFacade      : getUserInfo start OrderDTO{userId='1001', userInfo=null, goodId='2001', goodsInfo=null}
2021-03-13 11:34:26.551  INFO 13596 --- [nio-9010-exec-2] c.l.a.n.i.c.order.OrderServerFacade      : request get user info start 1001 
2021-03-13 11:34:26.595  INFO 13596 --- [nio-9010-exec-2] c.l.a.n.i.c.order.OrderServerFacade      : request goods start 2001 
2021-03-13 11:34:26.602  INFO 13596 --- [nio-9010-exec-2] c.l.a.n.i.c.order.OrderServerFacade      : getUserInfo  request 1001 rsponse true 
2021-03-13 11:34:26.602  INFO 13596 --- [nio-9010-exec-2] c.l.a.n.i.c.order.OrderServerFacade      : getGoods  request 2001 rsponse true 
2021-03-13 11:34:31.592  INFO 13596 --- [nio-9010-exec-3] c.l.a.n.i.c.order.OrderServerFacade      : /userCallBack  UserDTO{userId='1001', name='bug_null', address='this is shanghai'}
2021-03-13 11:34:36.605  INFO 13596 --- [nio-9010-exec-5] c.l.a.n.i.c.order.OrderServerFacade      : /goodscallBack Goods{dataId='2001', goodsPrice=1024, message='这是一个苹果,apple,还被咬了一口'}

```

从日志中可以看到一个调用的大至的时间关系，首先是nio-9010-exec-2这个线程发起了两个请求，并收到了通讯成功的响应。

在过了5秒后，nio-9010-exec-3收到了用户的回调结果。又过了5秒，nio-9010-exec-5收到了了商品的查询回调结果。

如果在这个处理过程中再采用打印线程栈的信息命令来查看时，没有工作线程处理阻塞状态。

那这时候我们再来画一个调用的线程模型。

![](D:\doc\博客\总续实践\非阻塞式调用\callback\callback线程模型.png)





### 3.4 总结

优点：

1. 采用回调机制时，客户端不需要等待服务器的响应，不需要同步等待或者使用线程等待。就能为执行其他任务留出了资源，可以提供更大的吞吐量。
2. 在回调机机中，客户端与服务器完全异步，这样两边就可以按照自己节奏处理任务。

缺点：

1. 时延的增加，将原来的一次网络操作，采用回调机制后变成了两次操作，一个请求和一个回复，这增加了一次网络的开销，必然带来响应时延的增加。
2. 代码的复杂性增加了很多，在示例中就可以看出来了，这就需要中间入一些数据的临时存储，并且还需要保证在多线程环境下的正确性。
3. 维护的成本也增加很多，将原来一个接口变成了多个接口，这就带来了更的维护的成本。





详细代码请参看我的github:

https://github.com/kkzfl22/demojava8/blob/master/src/main/java/com/liujun/asynchronous/nonblocking/invoke/callback/httpinterface/order/OrderServerFacade.java



