# java实现异步非阻塞的几种方式-消息中间件

## 4. 消息中间件

在前面已经实现了异步调用、接口回调两种形式，都较为复杂，需要自己控制多线程，那有没有简单点的方式来实现异步的消息通讯。这个当然是有的，可以使用消息中间件的形式，来实现整个消息的异步调用。还是以订单这个场景为示例来举个粟子。

![](D:\doc\博客\总续实践\非阻塞式调用\消息中间件\消息中间件调用序列图.png)



### 3.1 样例代码

订单服务的代码：

```java

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



