# java实现异步非阻塞的几种方式-同步阻塞的调用

## 1. 同步阻塞调用

在讲异步非阻塞之前还是先来说明同步阻塞的调用吧。明白了同步阻塞的调用，才能更好的明白异步非阻塞的调用。以一个示例来说明吧，这是一个非常常见的程序间的调用。

我们的程序对外提供当前的用户的订单详细查询的接口，订单接口先调用用户服务，获取当前的用户信息；再调用商品接口获取商品的详细信息。

就以这样一个示例程序来说明吧。

![](D:\doc\博客\总续实践\非阻塞式调用\阻塞式调用.png)

假设这个订单服务调用用户服务的时间是2秒，调用商品服务的时间是3秒，订单服务自身的处理时间是1秒，那整个处理流程所需的时间就是2+3+1=6秒。

### 1.1 样例代码

用户服务:

```java
/**
 * 模拟的用户服务 
 */
@RestController
@RequestMapping("/user")
public class UserService {

  /** 测试预期值 */
  public static final String CHECK_USER_ID = "1001";

  @RequestMapping(
      value = "/getUserInfo",
      method = {RequestMethod.POST})
  public ApiResponse getUserInfo(@RequestBody UserDTO input) {
    if (null != input && CHECK_USER_ID.equals(input.getUserId())) {
      UserDTO rsp = new UserDTO();
      rsp.setName("bug_null");
      rsp.setAddress("this is shanghai");
      rsp.setUserId(CHECK_USER_ID);

      ThreadUtils.sleep(5);

      return ApiResponse.ok(rsp);
    }
    return ApiResponse.fail();
  }
}

/**
 * 用户的传输实体
 */
public class UserDTO {
  /** 用户的id */
  private String userId;

  /** 用户的名称 */
  private String name;

  /** 地址信息 */
  private String address;

 ......
}


/**
 * 用户服务的springboot的入口
 */
@SpringBootApplication
public class UserApplication {
  public static void main(String[] args) {
    String[] argsNew = new String[] {"-- server.port=9000"};
    SpringApplication.run(UserApplication.class, argsNew);
  }
}
```



商品服务

```java
/**
 * 用户模拟商品服务
 */
@RestController
@RequestMapping("/goods")
public class GoodsService {

  /** 商品的id */
  public static final String GOODS_ID = "2001";

  @RequestMapping(
      value = "/getGoodsInfo",
      method = {RequestMethod.POST})
  public ApiResponse getUserInfo(@RequestBody GoodsDTO input) {
    if (null != input && GOODS_ID.equals(input.getDataId())) {
      GoodsDTO goods = new GoodsDTO();
      goods.setDataId(GOODS_ID);
      goods.setGoodsPrice(1024);
      goods.setMessage("这是一个苹果,apple,还被咬了一口");

      ThreadUtils.sleep(10);

      return ApiResponse.ok(goods);
    }
    return ApiResponse.fail();
  }
}

/**
 * 商品信息
 */
public class GoodsDTO {

  /** 商品的id */
  private String dataId;

  /** 商品的价格 */
  private Integer goodsPrice;

  /** 商品的描述 */
  private String message;

  ......
}

@SpringBootApplication
public class GoodsApplication {

  public static void main(String[] args) {
    String[] argsNew = new String[] {"-- server.port=9001"};
    SpringApplication.run(GoodsApplication.class, argsNew);
  }
}
```



订单服务

```java
@RestController
@RequestMapping("/order")
public class OrderServerFacade {

  private Logger logger = LoggerFactory.getLogger(OrderServerFacade.class);

  private Gson gson = new Gson();

  /** 获取连接处理对象 */
  private RestTemplate restTemplate = this.getRestTemplate();

  @RequestMapping(
      value = "/orderInfo",
      method = {RequestMethod.POST})
  public ApiResponse getUserInfo(@RequestBody OrderDTO order) {
    logger.info("getUserInfo start {}", order);
    // 用户信息
    ClientUserDTO userRsp = this.getUserInfo(order.getUserId());
    // 获取商品信息
    ClientGoodsDTO clientGoodRsp = this.getGoods(order.getGoodId());
    ThreadUtils.sleep(1);
    OrderDTO orderRsp = this.builderRsp(userRsp, clientGoodRsp);
    logger.info("getUserInfo start {} rsp {} orderRsp {}  ", order, orderRsp);
    // 构建结果的响应
    return ApiResponse.ok(orderRsp);
  }

  /**
   * 构造响应
   *
   * @param userInfo 用户信息
   * @return 当前响应的用户信息
   */
  private OrderDTO builderRsp(ClientUserDTO userInfo, ClientGoodsDTO goodsInfo) {
    OrderDTO order = new OrderDTO();
    order.setUserId(userInfo.getUserId());
    order.setUserInfo(userInfo);
    order.setGoodId(goodsInfo.getDataId());
    order.setGoodsInfo(goodsInfo);
    return order;
  }

  /**
   * 获取用户的信息
   *
   * @param userId 用户的id
   * @return 用户的信息
   */
  private ClientUserDTO getUserInfo(String userId) {

    logger.info("request get user info start {} ", userId);

    ClientUserDTO clientUser = new ClientUserDTO();
    clientUser.setUserId(userId);
    HttpHeaders headers = new HttpHeaders();
    // 将对象装入HttpEntity中
    HttpEntity<ClientUserDTO> request = new HttpEntity<>(clientUser, headers);
    ResponseEntity<String> result =
        restTemplate.postForEntity("http://localhost:9000/user/getUserInfo", request, String.class);
    if (HttpStatus.OK.value() == result.getStatusCodeValue()) {
      ApiResponse<ClientUserDTO> data =
          gson.fromJson(result.getBody(), new TypeToken<ApiResponse<ClientUserDTO>>() {}.getType());
      // 如果操作成功，则返回结果
      if (data.getResult()) {
        ClientUserDTO rsp = data.getData();
        logger.info("request get user info start {} rsp {} ", userId, rsp);
        return rsp;
      }
    }
    return null;
  }

  /**
   * 获取商品信息
   *
   * @param dataId 商品信息
   * @return 当前的用户的信息
   */
  private ClientGoodsDTO getGoods(String dataId) {

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
      ApiResponse<ClientGoodsDTO> data =
          gson.fromJson(
              result.getBody(), new TypeToken<ApiResponse<ClientGoodsDTO>>() {}.getType());
      // 如果操作成功，则返回结果
      if (data.getResult()) {
        ClientGoodsDTO goodsRsp = data.getData();
        logger.info("request goods start {} , response {} ", dataId, goodsRsp);
        return goodsRsp;
      }
    }
    return null;
  }

  /**
   * 获取连接管理对象
   *
   * @return
   */
  private RestTemplate getRestTemplate() {
    return new RestTemplate(getClientHttpRequestFactory());
  }

  /**
   * 获取连接处理的工厂信息
   *
   * @return
   */
  private SimpleClientHttpRequestFactory getClientHttpRequestFactory() {
    SimpleClientHttpRequestFactory clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
    clientHttpRequestFactory.setConnectTimeout(25000);
    clientHttpRequestFactory.setReadTimeout(25000);
    return clientHttpRequestFactory;
  }
}
```



### 1.2 测试

执行单元测试:

```java
/**
 * 订单服务查询
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(
    classes = {OrderApplication.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestOrderServerFacade {

  @Autowired private Gson gson;

  @Autowired protected TestRestTemplate restTemplate;

  @Test
  public void testUser() {
    OrderDTO orderInfo = new OrderDTO();
    orderInfo.setGoodId("2001");
    orderInfo.setUserId("1001");
    // 将对象装入HttpEntity中
    HttpEntity<OrderDTO> request = new HttpEntity<>(orderInfo);
    ResponseEntity<String> result =
        restTemplate.postForEntity("/order/orderInfo", request, String.class);
    Assert.assertEquals(HttpStatus.OK.value(), result.getStatusCodeValue());
    ApiResponse<OrderDTO> data =
        gson.fromJson(result.getBody(), new TypeToken<ApiResponse<OrderDTO>>() {}.getType());
    System.out.println(data);
    Assert.assertEquals(data.getResult(), Boolean.TRUE);
    Assert.assertEquals(data.getCode(), APICodeEnum.SUCCESS.getErrorData().getCode());
    Assert.assertNotNull(data.getData().getGoodsInfo());
    Assert.assertNotNull(data.getData().getUserInfo());
  }
}
```

单元测试结果

![](D:\doc\博客\总续实践\非阻塞式调用\同步阻塞式调用的结果.png)

从结果中耗时是16秒多。也证实了在开始时我的一个预期。同步阻塞式调用。由于只能顺序式调用。所以总时间就是所有调用的总时间相加。





### 1.3 分析

那当以客户端去调用服务时，客户端都在做什么呢？

这个时候可以借助于java的命令，拿到线程的栈信息和java堆信息

```java
D:\run\dump>jps -l
13568 com.liujun.asynchronous.nonblocking.common.goods.GoodsApplication
1456 com.liujun.asynchronous.nonblocking.invoke.synchronous.OrderApplication
14848 org.jetbrains.jps.cmdline.Launcher
13604 org.jetbrains.kotlin.daemon.KotlinCompileDaemon
3508 com.liujun.asynchronous.nonblocking.common.user.UserApplication
5556 org.jetbrains.idea.maven.server.RemoteMavenServer36
6996
12840 org/netbeans/Main
14840
16092 sun.tools.jps.Jps

    
D:\run\dump>jstack -l 1456 > order.threaddump

```



查看日志

```java
2021-03-09 10:39:01.591  INFO 1456 --- [nio-9010-exec-6] c.l.a.n.i.synchronous.OrderServerFacade  : getUserInfo start OrderDTO{userId='1001', userInfo=null, goodId='2001', goodsInfo=null}
2021-03-09 10:39:01.591  INFO 1456 --- [nio-9010-exec-6] c.l.a.n.i.synchronous.OrderServerFacade  : request get user info start 1001 
2021-03-09 10:39:03.596  INFO 1456 --- [nio-9010-exec-6] c.l.a.n.i.synchronous.OrderServerFacade  : request get user info start 1001 rsp UserDTO{userId='1001', name='bug_null', address='this is shanghai'} 
2021-03-09 10:39:03.597  INFO 1456 --- [nio-9010-exec-6] c.l.a.n.i.synchronous.OrderServerFacade  : request goods start 2001 
2021-03-09 10:39:13.603  INFO 1456 --- [nio-9010-exec-6] c.l.a.n.i.synchronous.OrderServerFacade  : request goods start 2001 , response Goods{dataId='2001', goodsPrice=1024, message='这是一个苹果,apple,还被咬了一口'} 
2021-03-09 10:39:14.603  INFO 1456 --- [nio-9010-exec-6] c.l.a.n.i.synchronous.OrderServerFacade  : getUserInfo start OrderDTO{userId='1001', userInfo=null, goodId='2001', goodsInfo=null} rsp OrderDTO{userId='1001', userInfo=UserDTO{userId='1001', name='bug_null', address='this is shanghai'}, goodId='2001', goodsInfo=Goods{dataId='2001', goodsPrice=1024, message='这是一个苹果,apple,还被咬了一口'}} orderRsp {}   
 

```

从日志可以看任务是顺序式的调用。所以整个的调用时间就是所有执行的累加。

在本例中，配制的信息仅提取了线程的后15位信息，但已经可以定位到调用的线程了，现在去查看线程的调用栈信息.

```java
"http-nio-9010-exec-6" #29 daemon prio=5 os_prio=0 tid=0x000000002129d000 nid=0x3ab0 runnable [0x000000002338c000]
   java.lang.Thread.State: RUNNABLE
	at java.net.SocketInputStream.socketRead0(Native Method)
	at java.net.SocketInputStream.socketRead(SocketInputStream.java:116)
	at java.net.SocketInputStream.read(SocketInputStream.java:171)
	at java.net.SocketInputStream.read(SocketInputStream.java:141)
	at java.io.BufferedInputStream.fill(BufferedInputStream.java:246)
	at java.io.BufferedInputStream.read1(BufferedInputStream.java:286)
	at java.io.BufferedInputStream.read(BufferedInputStream.java:345)
	- locked <0x00000006c5a0a588> (a java.io.BufferedInputStream)
	at sun.net.www.http.HttpClient.parseHTTPHeader(HttpClient.java:735)
	at sun.net.www.http.HttpClient.parseHTTP(HttpClient.java:678)
	at sun.net.www.protocol.http.HttpURLConnection.getInputStream0(HttpURLConnection.java:1593)
	- locked <0x00000006c5a0a638> (a sun.net.www.protocol.http.HttpURLConnection)
	at sun.net.www.protocol.http.HttpURLConnection.getInputStream(HttpURLConnection.java:1498)
	- locked <0x00000006c5a0a638> (a sun.net.www.protocol.http.HttpURLConnection)
	at java.net.HttpURLConnection.getResponseCode(HttpURLConnection.java:480)
	at org.springframework.http.client.SimpleClientHttpResponse.getRawStatusCode(SimpleClientHttpResponse.java:55)
	at org.springframework.web.client.DefaultResponseErrorHandler.hasError(DefaultResponseErrorHandler.java:55)
	at org.springframework.web.client.RestTemplate.handleResponse(RestTemplate.java:766)
	at org.springframework.web.client.RestTemplate.doExecute(RestTemplate.java:736)
	at org.springframework.web.client.RestTemplate.execute(RestTemplate.java:670)
	at org.springframework.web.client.RestTemplate.postForEntity(RestTemplate.java:445)
	at com.liujun.asynchronous.nonblocking.invoke.synchronous.OrderServerFacade.getGoods(OrderServerFacade.java:124)
	at com.liujun.asynchronous.nonblocking.invoke.synchronous.OrderServerFacade.getUserInfo(OrderServerFacade.java:50)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.springframework.web.method.support.InvocableHandlerMethod.doInvoke(InvocableHandlerMethod.java:190)
	at org.springframework.web.method.support.InvocableHandlerMethod.invokeForRequest(InvocableHandlerMethod.java:138)
	at org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod.invokeAndHandle(ServletInvocableHandlerMethod.java:104)
	at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.invokeHandlerMethod(RequestMappingHandlerAdapter.java:892)
	at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.handleInternal(RequestMappingHandlerAdapter.java:797)
	at org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter.handle(AbstractHandlerMethodAdapter.java:87)
	at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:1039)
	at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:942)
	at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:1005)
	at org.springframework.web.servlet.FrameworkServlet.doPost(FrameworkServlet.java:908)
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:660)
	at org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:882)
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:741)
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:231)
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)
	at org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:53)
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)
	at org.springframework.web.filter.RequestContextFilter.doFilterInternal(RequestContextFilter.java:99)
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:118)
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)
	at org.springframework.web.filter.FormContentFilter.doFilterInternal(FormContentFilter.java:92)
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:118)
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)
	at org.springframework.web.filter.HiddenHttpMethodFilter.doFilterInternal(HiddenHttpMethodFilter.java:93)
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:118)
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)
	at org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:200)
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:118)
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)
	at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:202)
	at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:96)
	at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:490)
	at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:139)
	at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:92)
	at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:74)
	at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:343)
	at org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:408)
	at org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:66)
	at org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:853)
	at org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1587)
	at org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:49)
	- locked <0x00000006c59c6e10> (a org.apache.tomcat.util.net.NioEndpoint$NioSocketWrapper)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:61)
	at java.lang.Thread.run(Thread.java:748)

   Locked ownable synchronizers:
	- <0x00000006c53ad000> (a java.util.concurrent.ThreadPoolExecutor$Worker)

```

IO会阻塞在：java.net.SocketInputStream#socketRead0 的native方法上。

通过分析，就可以知道同步调用的一个线程模型：

![](D:\doc\博客\总续实践\非阻塞式调用\同步阻塞线程模型.png)

### 1.4 总结

优点：

这应该是编程最简单的模型了。调用链非常的清楚，简单直接。不需要其他复杂的机制来解决异步所带来的问题，等着结果即可，所以在很大一部分业务场景中，采用的都是此调用模型。

劣势：

同步阻塞的最大问题在于在调用过程中线程处于等待状态，线程的资源没有充分的利用，对于服务器端应用来说，会限并发的用户数。





这是最基础的同步调用模型，详细代码可查看我的github:

https://github.com/kkzfl22/demojava8/blob/master/src/main/java/com/liujun/asynchronous/nonblocking/invoke/synchronous/OrderServerFacade.java





