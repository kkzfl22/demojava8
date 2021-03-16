# java实现异步非阻塞的几种方式-异步调用

异步调用，就相关于将原来同步调用的代码块放置到一个又一个的线程中去执行，这样就可以并行的执行相关的任务。也就不用卡在一个点的等待，进而影响后续的任务。还以是订单为示例，演示整个调用的过程。

## 2. 异步调用

![](D:\doc\博客\总续实践\非阻塞式调用\异步阻塞调用\异步阻塞调用的序列图.png)

使用异步调用与同步调用最大的不同点，在于加入了线程池，由于任务是没有先后顺序的，可将用户查询的请和商品查询的请求都加入到线程池中，使用两个线程等待结果即可。



### 2.1 样例代码

首先加入的就是线程池

```
public class ScheduleTaskThreadPool {

  /** 进行任务调度的线程池对象 */
  public static final ScheduleTaskThreadPool INSTANCE = new ScheduleTaskThreadPool();

  /**
   * 线程池中的核心线程数，当提交一个任务时，线程池创建一个新线程执行任务，直到当前线程数等于corePoolSize；
   * 如果当前线程数为corePoolSize，继续提交的任务被保存到阻塞队列中，等待被执行；
   * 如果执行了线程池的prestartAllCoreThreads()方法，线程池会提前创建并启动所有核心线程。
   */
  private static final int MIN_THREAD_NUM = 2;

  /**
   * 线程池中允许的最大线程数。如果当前阻塞队列满了，且继续提交任务，则创建新的线程执行任务，
   *
   * <p>前提是当前线程数小于maximumPoolSize
   */
  private static final int MAX_THREAD_NUM = 4;

  /**
   * 线程池中允许的最大线程数。如果当前阻塞队列满了，且继续提交任务，则创建新的线程执行任务，
   *
   * <p>前提是当前线程数小于maximumPoolSize
   */
  private static final int WAIT_NUM = 8;

  /**
   * 线程空闲时的存活时间，即当线程没有任务执行时，继续存活的时间。以秒为单位
   *
   * <p>默认情况下，该参数只在线程数大于corePoolSize时才有用
   */
  private static final int KEEPALIVE = 5;

  /**
   * workQueue必须是BlockingQueue阻塞队列。当线程池中的线程数超过它的corePoolSize的时候，
   *
   * <p>线程会进入阻塞队列进行阻塞等待。 通过workQueue，线程池实现了阻塞功能 1,（1）不排队，直接提交 将任务直接交给线程处理而不保持它们，可使用SynchronousQueue
   * 如果不存在可用于立即运行任务的线程（即线程池中的线程都在工作），则试图把任务加入缓冲队列将会失败，
   * 因此会构造一个新的线程来处理新添加的任务，并将其加入到线程池中（corePoolSize-->maximumPoolSize扩容）
   * Executors.newCachedThreadPool()采用的便是这种策略
   *
   * <p>（2）无界队列 可以使用LinkedBlockingQueue（基于链表的有界队列，FIFO），理论上是该队列可以对无限多的任务排队
   * 将导致在所有corePoolSize线程都工作的情况下将新任务加入到队列中。这样， 创建的线程就不会超过corePoolSize，也因此，maximumPoolSize的值也就无效了
   *
   * <p>（3）有界队列 可以使用ArrayBlockingQueue（基于数组结构的有界队列，FIFO），并指定队列的最大长度
   * 使用有界队列可以防止资源耗尽，但也会造成超过队列大小和maximumPoolSize后，提交的任务被拒绝的问题，
   *
   * <p>比较难调整和控制。
   *
   * <p>等待任务的队列
   */
  private ArrayBlockingQueue queue = new ArrayBlockingQueue(WAIT_NUM);

  /**
   * 策略说明 1,ThreadPoolExecutor.AbortPolicy:丢弃任务并抛出RejectedExecutionException异常。
   *
   * <p>2,ThreadPoolExecutor.DiscardPolicy：也是丢弃任务，但是不抛出异常。
   *
   * <p>3,ThreadPoolExecutor.DiscardOldestPolicy：丢弃队列最前面的任务，然后重新尝试执行任务（重复此过程）
   *
   * <p>4,ThreadPoolExecutor.CallerRunsPolicy：由调用线程处理该任务,这个策略重试添加当前的任务，他会自动重复调用 execute() 方法，直到成功。
   */
  private ThreadPoolExecutor pool =
      new ThreadPoolExecutor(
          MIN_THREAD_NUM,
          MAX_THREAD_NUM,
          KEEPALIVE,
          TimeUnit.SECONDS,
          queue,
          new ThreadFactoryBuilder().setNameFormat("order-%d").setDaemon(true).build(),
          new ThreadPoolExecutor.AbortPolicy());

  /**
   * 提交带返回值的线程给线程池来运行
   *
   * @param task
   */
  public Future<?> submit(Callable task) {
    return pool.submit(task);
  }

  /**
   * 提交任务给线程池运行
   *
   * @param task
   */
  public Future<?> submit(Runnable task) {
    return pool.submit(task);
  }

  /**
   * 当前否为线程池已经满载
   *
   * @return
   */
  public boolean isFull() {
    return pool.getPoolSize() == pool.getMaximumPoolSize();
  }

  public String outPoolInfo() {
    StringBuilder outData = new StringBuilder();
    outData.append("pool Core Size:").append(pool.getCorePoolSize()).append(",");
    outData.append("curr pool size:").append(pool.getPoolSize()).append(",");
    outData.append("max pool Size:").append(pool.getMaximumPoolSize()).append(",");
    outData.append("queue size:").append(pool.getQueue().size()).append(",");
    outData.append("task completed size:").append(pool.getCompletedTaskCount()).append(",");
    outData.append("active count size:").append(pool.getActiveCount()).append(",");
    outData.append("task count size:").append(pool.getTaskCount()).append(",");
    outData.append(Symbol.LINE);
    outData.append(Symbol.LINE);

    return outData.toString();
  }

  public void shutdown() {
    pool.shutdown();
  }
}
```



order的改造

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
    Future userRsp = this.asynchronousUser(order.getUserId());
    // 商品信息
    Future goodsRsp = this.asynchronousGoods(order.getGoodId());

    OrderDTO orderRsp = null;
    try {
      orderRsp = this.builderRsp(order, userRsp, goodsRsp);
    } catch (ExecutionException e) {
      logger.error("get userRps : ExecutionException  ", e);
    } catch (InterruptedException e) {
      logger.error("get userRps : InterruptedException  ", e);
    }

    ThreadUtils.sleep(1);

    logger.info("getUserInfo start {} rsp {} orderRsp {}  ", order, orderRsp);

    // 构建结果的响应
    return ApiResponse.ok(orderRsp);
  }

  /**
   * 异步调用用户服务
   *
   * @param goods 商品信息
   * @return 商品的结果
   */
  public Future asynchronousGoods(String goods) {
    Future rspData =
        ScheduleTaskThreadPool.INSTANCE.submit(
            () -> {
              return this.getGoods(goods);
            });

    return rspData;
  }

  /**
   * 异步调用用户
   *
   * @param userId
   * @return
   */
  public Future asynchronousUser(String userId) {
    Future rspData =
        ScheduleTaskThreadPool.INSTANCE.submit(
            () -> {
              return this.getUserInfo(userId);
            });

    return rspData;
  }

  /**
   * 构造响应
   *
   * @param userInfo 用户信息
   * @return 当前响应的用户信息
   */
  private OrderDTO builderRsp(OrderDTO orderInfo, Future userInfo, Future goodsInfo)
      throws ExecutionException, InterruptedException {
    OrderDTO order = new OrderDTO();
    order.setUserId(orderInfo.getUserId());
    order.setUserInfo((ClientUserDTO) userInfo.get());
    order.setGoodId(orderInfo.getGoodId());
    order.setGoodsInfo((ClientGoodsDTO) goodsInfo.get());
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

相比于同步阻塞的调用，用户查询与商品的查询都提交至了线程池中去执行发送请求的任务，并等待结果的响应，线程池中任务的执行结果使用Future来获取，这是一个等待的操作。





再来个启动函数

```java
@SpringBootApplication
public class OrderApplication {

  public static void main(String[] args) {
    String[] argsNew = new String[] {"-- server.port=9010"};
    SpringApplication.run(OrderApplication.class, argsNew);
  }
}
```



我们做一个新的服务来测试下。

### 2.2 测试

单元测试的代码与同步阻塞相同.

现在来看下执行结果。

![](D:\doc\博客\总续实践\非阻塞式调用\异步阻塞调用\异步阻塞式调用的结果.png)

从结果中可以看到，新的调用都已经成功了，总耗时相比于同步调用，减少了5秒。仅耗时11秒。

### 2.3 分析

先看日志做一个基本的了解：

```java
2021-03-12 07:52:45.281  INFO 1276 --- [nio-9010-exec-6] c.l.a.n.i.threadpool.OrderServerFacade   : getUserInfo start OrderDTO{userId='1001', userInfo=null, goodId='2001', goodsInfo=null}
2021-03-12 07:52:45.281  INFO 1276 --- [        order-0] c.l.a.n.i.threadpool.OrderServerFacade   : request get user info start 1001 
2021-03-12 07:52:45.281  INFO 1276 --- [        order-1] c.l.a.n.i.threadpool.OrderServerFacade   : request goods start 2001 
2021-03-12 07:52:50.287  INFO 1276 --- [        order-0] c.l.a.n.i.threadpool.OrderServerFacade   : request get user info start 1001 rsp UserDTO{userId='1001', name='bug_null', address='this is shanghai'} 
2021-03-12 07:52:55.287  INFO 1276 --- [        order-1] c.l.a.n.i.threadpool.OrderServerFacade   : request goods start 2001 , response Goods{dataId='2001', goodsPrice=1024, message='这是一个苹果,apple,还被咬了一口'} 
2021-03-12 07:52:56.288  INFO 1276 --- [nio-9010-exec-6] c.l.a.n.i.threadpool.OrderServerFacade   : getUserInfo start OrderDTO{userId='1001', userInfo=null, goodId='2001', goodsInfo=null} rsp OrderDTO{userId='1001', userInfo=UserDTO{userId='1001', name='bug_null', address='this is shanghai'}, goodId='2001', goodsInfo=Goods{dataId='2001', goodsPrice=1024, message='这是一个苹果,apple,还被咬了一口'}} orderRsp {}  
```

可以发现任务被分配到两个线程去执行。分别为order-0和order-1，然后等待结果响应。待所有响应都结束了，返回整个的调用结果。

同样的借助于jstack工具，查看线程栈的信息。

```java
"order-1" #44 daemon prio=5 os_prio=0 tid=0x00000000210cc800 nid=0x534 runnable [0x0000000000d2e000]
   java.lang.Thread.State: RUNNABLE
	at java.net.SocketInputStream.socketRead0(Native Method)
	at java.net.SocketInputStream.socketRead(SocketInputStream.java:116)
	at java.net.SocketInputStream.read(SocketInputStream.java:171)
	at java.net.SocketInputStream.read(SocketInputStream.java:141)
	at java.io.BufferedInputStream.fill(BufferedInputStream.java:246)
	at java.io.BufferedInputStream.read1(BufferedInputStream.java:286)
	at java.io.BufferedInputStream.read(BufferedInputStream.java:345)
	- locked <0x000000076c5da8f8> (a java.io.BufferedInputStream)
	at sun.net.www.http.HttpClient.parseHTTPHeader(HttpClient.java:735)
	at sun.net.www.http.HttpClient.parseHTTP(HttpClient.java:678)
	at sun.net.www.protocol.http.HttpURLConnection.getInputStream0(HttpURLConnection.java:1593)
	- locked <0x000000076c5c97f0> (a sun.net.www.protocol.http.HttpURLConnection)
	at sun.net.www.protocol.http.HttpURLConnection.getInputStream(HttpURLConnection.java:1498)
	- locked <0x000000076c5c97f0> (a sun.net.www.protocol.http.HttpURLConnection)
	at java.net.HttpURLConnection.getResponseCode(HttpURLConnection.java:480)
	at org.springframework.http.client.SimpleClientHttpResponse.getRawStatusCode(SimpleClientHttpResponse.java:55)
	at org.springframework.web.client.DefaultResponseErrorHandler.hasError(DefaultResponseErrorHandler.java:55)
	at org.springframework.web.client.RestTemplate.handleResponse(RestTemplate.java:766)
	at org.springframework.web.client.RestTemplate.doExecute(RestTemplate.java:736)
	at org.springframework.web.client.RestTemplate.execute(RestTemplate.java:670)
	at org.springframework.web.client.RestTemplate.postForEntity(RestTemplate.java:445)
	at com.liujun.asynchronous.nonblocking.invoke.threadpool.OrderServerFacade.getGoods(OrderServerFacade.java:168)
	at com.liujun.asynchronous.nonblocking.invoke.threadpool.OrderServerFacade.lambda$asynchronousGoods$0(OrderServerFacade.java:82)
	at com.liujun.asynchronous.nonblocking.invoke.threadpool.OrderServerFacade$$Lambda$436/1379641878.call(Unknown Source)
	at java.util.concurrent.FutureTask.run(FutureTask.java:266)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at java.lang.Thread.run(Thread.java:748)

   Locked ownable synchronizers:
	- <0x00000006c560ee68> (a java.util.concurrent.ThreadPoolExecutor$Worker)
        
        
"order-0" #43 daemon prio=5 os_prio=0 tid=0x00000000210cf000 nid=0x1954 runnable [0x0000000000c2e000]
   java.lang.Thread.State: RUNNABLE
	at java.net.SocketInputStream.socketRead0(Native Method)
	at java.net.SocketInputStream.socketRead(SocketInputStream.java:116)
	at java.net.SocketInputStream.read(SocketInputStream.java:171)
	at java.net.SocketInputStream.read(SocketInputStream.java:141)
	at java.io.BufferedInputStream.fill(BufferedInputStream.java:246)
	at java.io.BufferedInputStream.read1(BufferedInputStream.java:286)
	at java.io.BufferedInputStream.read(BufferedInputStream.java:345)
	- locked <0x000000076c9eaec8> (a java.io.BufferedInputStream)
	at sun.net.www.http.HttpClient.parseHTTPHeader(HttpClient.java:735)
	at sun.net.www.http.HttpClient.parseHTTP(HttpClient.java:678)
	at sun.net.www.protocol.http.HttpURLConnection.getInputStream0(HttpURLConnection.java:1593)
	- locked <0x000000076c9da368> (a sun.net.www.protocol.http.HttpURLConnection)
	at sun.net.www.protocol.http.HttpURLConnection.getInputStream(HttpURLConnection.java:1498)
	- locked <0x000000076c9da368> (a sun.net.www.protocol.http.HttpURLConnection)
	at java.net.HttpURLConnection.getResponseCode(HttpURLConnection.java:480)
	at org.springframework.http.client.SimpleClientHttpResponse.getRawStatusCode(SimpleClientHttpResponse.java:55)
	at org.springframework.web.client.DefaultResponseErrorHandler.hasError(DefaultResponseErrorHandler.java:55)
	at org.springframework.web.client.RestTemplate.handleResponse(RestTemplate.java:766)
	at org.springframework.web.client.RestTemplate.doExecute(RestTemplate.java:736)
	at org.springframework.web.client.RestTemplate.execute(RestTemplate.java:670)
	at org.springframework.web.client.RestTemplate.postForEntity(RestTemplate.java:445)
	at com.liujun.asynchronous.nonblocking.invoke.threadpool.OrderServerFacade.getUserInfo(OrderServerFacade.java:136)
	at com.liujun.asynchronous.nonblocking.invoke.threadpool.OrderServerFacade.lambda$asynchronousUser$1(OrderServerFacade.java:98)
	at com.liujun.asynchronous.nonblocking.invoke.threadpool.OrderServerFacade$$Lambda$435/1688648333.call(Unknown Source)
	at java.util.concurrent.FutureTask.run(FutureTask.java:266)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at java.lang.Thread.run(Thread.java:748)

   Locked ownable synchronizers:
	- <0x00000006c560ec38> (a java.util.concurrent.ThreadPoolExecutor$Worker)
        


"http-nio-9010-exec-6" #29 daemon prio=5 os_prio=0 tid=0x00000000210bc000 nid=0x2e58 waiting on condition [0x0000000022f7c000]
   java.lang.Thread.State: WAITING (parking)
	at sun.misc.Unsafe.park(Native Method)
	- parking to wait for  <0x000000076cb6ba28> (a java.util.concurrent.FutureTask)
	at java.util.concurrent.locks.LockSupport.park(LockSupport.java:175)
	at java.util.concurrent.FutureTask.awaitDone(FutureTask.java:429)
	at java.util.concurrent.FutureTask.get(FutureTask.java:191)
	at com.liujun.asynchronous.nonblocking.invoke.threadpool.OrderServerFacade.builderRsp(OrderServerFacade.java:114)
	at com.liujun.asynchronous.nonblocking.invoke.threadpool.OrderServerFacade.getUserInfo(OrderServerFacade.java:57)
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
	- locked <0x00000006c544acb8> (a org.apache.tomcat.util.net.NioEndpoint$NioSocketWrapper)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:61)
	at java.lang.Thread.run(Thread.java:748)

   Locked ownable synchronizers:
	- <0x00000006c53e9668> (a java.util.concurrent.ThreadPoolExecutor$Worker)        

```

从栈的信息中可以看中。主线程会阻塞在获取结果响应中，也就是	- parking to wait for  <0x000000076cb6ba28> (a java.util.concurrent.FutureTask)这个对象中，是线程的调用结果对象。而提交到线程池的两个任务，则会阻塞在：at java.net.SocketInputStream.socketRead0(Native Method)对象中，与同步阻塞的调用示例相同。

通过分析，我们就可以得到异步阻塞的一个示例图。

![](D:\doc\博客\总续实践\非阻塞式调用\异步阻塞调用\异步阻塞的调用模型.png)

### 2.4 总结

优点：

此调用模型，将原本串行的任务，拆分到一个或者多个线程中去运行。这样任务就不用串行等待。提高了请求的响应时间，示例代码就从原本的16秒，减少到了11秒，省掉了一个请求的时间。任务中需要执行的操作越多。那么所提高的响应时间也是越好。

缺点：

那也很明显，首先是调用中的线程还是处于IO blocking中，这也导致了资源还是被浪费在了等待上；其次一个机器所能打开的线程有是限的。不能无限制的开线程，线程过多，将导致频繁的上下文切换。造成更大的资源浪费；再就是一个并行的场景存在限制，任务必须能够并行执行。不存在先后的关系，如果业务上存在在着先后顺序，就不能采用。





详细代码请至我的github:

https://github.com/kkzfl22/demojava8/blob/master/src/main/java/com/liujun/asynchronous/nonblocking/invoke/threadpool/OrderServerFacade.java

