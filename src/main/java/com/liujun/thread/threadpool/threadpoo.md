## java踩坑之线程池
就有前几天刚完成的大型对比时使用到了线程池，对于线程池，我想我有好多踩过的坑的经历，这也就是所说的经验吧，我想通过这篇文章，将我所踩过的坑，都记录下来.

## 坑1-newFixedThreadPool

### 程序问题演示:

这是jdk提供的一个默认线程线程池的实现。创建一组固定大小的线程池，来运行任务。当时这个任务是用来运行后台的异步发送通知任务。每秒大约100个发送量，高峰时段大约500个发送量，量并不大。但有一天通知服务突然宕机了，而我们服务调用超时的时间是2分钟，结果就导致我们的服务出现了一个OOM(OutOfMemoryError)的错误，我将用一个模拟程序演示整个过程。

请看下面的模拟程序:

```java
public class TestDataNewFixedThreadPool {

  /** 填充数据个数 */
  private static final int MAX_DATA_FULL = 512;

  /**
   * 添加jmx参数 -Xmx1G -Xms1G -Dcom.sun.management.jmxremote.port=7091
   * -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false
   *
   * @throws InterruptedException
   */
  @Test
  public void testThreadPool() {
    ExecutorService dataPool = Executors.newFixedThreadPool(4);
    for (int i = 0; i < Integer.MAX_VALUE; i++) {
      dataPool.submit(new DataSender(fullData()));
    }
  }

  /**
   * 数据的填充操作
   *
   * @return 字符信息
   */
  private String fullData() {
    StringBuilder dataMsg = new StringBuilder();
    for (int j = 0; j < MAX_DATA_FULL; j++) {
      dataMsg.append(ThreadLocalRandom.current().nextInt());
    }
    return dataMsg.toString();
  }

  /** 用来模拟数据发送的任务 */
  public static class DataSender implements Runnable {
    private String data;

    public DataSender(String data) {
      this.data = data;
    }

    @Override
    public void run() {
      // 使用线程休眠来模拟发送,由于发生了了宕机，发送将不可用。
      try {
        System.out.println("任务执行");
        Thread.sleep(120000L);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      System.out.println("data final:" + data);
    }
  }
}
```

当程序执行后不久，就能在控制台中看到OOM：

```java
任务执行
任务执行
任务执行
任务执行
java.lang.OutOfMemoryError: GC overhead limit exceeded
Exception in thread "main" java.lang.OutOfMemoryError: GC overhead limit exceeded
```

打开newFixedThreadPool源码一看究竟：

```java
public static ExecutorService newFixedThreadPool(int nThreads) {
    return new ThreadPoolExecutor(nThreads, nThreads,
                                  0L, TimeUnit.MILLISECONDS,
                                  new LinkedBlockingQueue<Runnable>());
}

```

在newFixedThreadPool方法中，可以看到传入了一个固定的线程数作为线程池的运行参数，线程的保活时间0秒，也就是不进行保活处理。然后再使用了一个LinkedBlockingQueue的队列，当翻看LinkedBlockingQueue的默认构建方法时，发现:

```java
    /**
     * Creates a {@code LinkedBlockingQueue} with a capacity of
     * {@link Integer#MAX_VALUE}.
     */
    public LinkedBlockingQueue() {
        this(Integer.MAX_VALUE);
    }
```

使用的是Integer.MAX_VALUE，可以认为这就是一个无界队列。

至此，发生OOM问题的源头就找到了，在通知服务的宕机期间，有大量的用户操作调用了下发通知，我们将下发通知任务加入了线程池中，而线程池中的线程都已经在运行任务了，而任务是需要2分钟的时间才能超时获得结果 ，这时候线程只能加入将任务加入线程池的队列。由于这个队列是一个无界队列，大量的下发通知积压，撑爆内存，导致了OOM.



### 解决方案:

由于这个问题是发下服务的宕机导致了线程池中的任务非常的慢，再加上队列是无界的，所以导致的OOM，要解决这个OOM问题也很容量，不要使用无界队列，使用有界队列来替代这个无界队列即可。

```java
  @Test
  public void testThreadPoolOK() {
    ThreadPoolExecutor dataPool =
        new ThreadPoolExecutor(
            4,
            4,
            0,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(8),
            new TaskThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy());
    for (int i = 0; i < Integer.MAX_VALUE; i++) {
      dataPool.submit(new DataSender(fullData()));
    }
  }
```

还是刚刚的发送程序。一会的堆积，线程池就执行了拒绝策略。提交任务至线程池就会失败。不会造成OOM程序崩溃这么严重的问题了。

```
任务执行
任务执行
任务执行
任务执行

java.util.concurrent.RejectedExecutionException: Task java.util.concurrent.FutureTask@224aed64 rejected from java.util.concurrent.ThreadPoolExecutor@c39f790[Running, pool size = 4, active threads = 4, queued tasks = 8, completed tasks = 0]

	at java.util.concurrent.ThreadPoolExecutor$AbortPolicy.rejectedExecution(ThreadPoolExecutor.java:2063)
	at java.util.concurrent.ThreadPoolExecutor.reject(ThreadPoolExecutor.java:830)
	at java.util.concurrent.ThreadPoolExecutor.execute(ThreadPoolExecutor.java:1379)
	at java.util.concurrent.AbstractExecutorService.submit(AbstractExecutorService.java:112)
	at com.liujun.thread.threadpool.problem.newfixpool.TestThreadPool.testThreadPoolOK(TestDataNewFixedThreadPool.java:50)
```



讲完了**FixedThreadPool**的一些问题，还有一个与此问题是一样的，那就是：**SingleThreadExecutor**



```
    public static ExecutorService newSingleThreadExecutor() {
        return new FinalizableDelegatedExecutorService
            (new ThreadPoolExecutor(1, 1,
                                    0L, TimeUnit.MILLISECONDS,
                                    new LinkedBlockingQueue<Runnable>()));
    }
    
    public LinkedBlockingQueue() {
        this(Integer.MAX_VALUE);
    }
```

一个无界的队列方式。





## 坑2-newCachedThreadPool

### 问题演示:

讲完了无界队列的坑，再来说一个坑。这个坑不是我踩的，是隔壁项目组踩的。

同样的，也来看直模拟程序吧

```java
  @Test
  public void testNewCacheThreadPoolOom() {
    ExecutorService dataPool = Executors.newCachedThreadPool();
    for (int i = 0; i < Integer.MAX_VALUE; i++) {
      dataPool.submit(new DataSender(fullData()));
    }
  }
```

在这段程序执行后不久，就发生了一个OOM:

```java
java.lang.OutOfMemoryError: unable to create new native thread

	at java.lang.Thread.start0(Native Method)
	at java.lang.Thread.start(Thread.java:717)
	at java.util.concurrent.ThreadPoolExecutor.addWorker(ThreadPoolExecutor.java:957)
	at java.util.concurrent.ThreadPoolExecutor.execute(ThreadPoolExecutor.java:1378)
	at java.util.concurrent.AbstractExecutorService.submit(AbstractExecutorService.java:112)
	at com.liujun.thread.threadpool.problem.newfixpool.TestThreadPool.testNewCacheThreadPoolOom(TestThreadPool.java:52)

Java HotSpot(TM) 64-Bit Server VM warning: Attempt to allocate stack guard pages failed.
Java HotSpot(TM) 64-Bit Server VM warning: Attempt to allocate stack guard pages failed.
```

还是同样的打开newCachedThreadPool源码一查究竟:

```java
    public static ExecutorService newCachedThreadPool() {
        return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                                      60L, TimeUnit.SECONDS,
                                      new SynchronousQueue<Runnable>());
    }
```

看到这段代码心里是不是在想心中千万个尼玛在奔腾。。。

这里是创建核心线程数为0，最大线程数是Integer.MAX_VALUE，换句话说就是允许无限制创建线程。线程有60秒的保活期，还使用了一个SynchronousQueue队列，这是一个阻塞队列，在JDK官方的API中是这样解释的:

>一种[阻塞队列](../../../java/util/concurrent/BlockingQueue.html)，其中每个插入操作必须等待另一个线程的对应移除操作  ，反之亦然。同步队列没有任何内部容量，甚至连一个队列的容量都没有

那也就是说，每次向队列中加入一个任务，都必须找到一个线程来执行，由于线程是无界的，就会无限制的创建线程。大量的线程创建堆积，最终导致了OOM.

![](D:\doc\博客\数据结构与算法\多线程\线程池的坑\线程池的坑-newCacheThreadPool.png)

此问题的OOM问题的源头也找到了，由于任务会大量的积压，大量的积压导致创建大量的线程，而我们都知道，线程是需要分配一定的内存究竟作为线程栈的。无限制的创建线程必须导致OOM.



### 解决方案:

这个问题的解决方案与newFixedThreadPool是一样的。将核心线程数与最大线程数，固定下来，设置任务队列大小，及拒绝策略后就可以了。







newCachedThreadPool的问题讲完成了，还一个问题一样的,那就是**ScheduledThreadPool** 

```java
    public static ScheduledExecutorService newScheduledThreadPool(int corePoolSize) {
        return new ScheduledThreadPoolExecutor(corePoolSize);
    }

    public ScheduledThreadPoolExecutor(int corePoolSize) {
        super(corePoolSize, Integer.MAX_VALUE, 0, NANOSECONDS,
              new DelayedWorkQueue());
    }
```









## 坑3-混用线程池

### 问题演示:

当时我们项目组需要开发一个数据汇聚的展示程序。数据汇聚展示呢需要用到CPU做大量的计算，并不涉及IO操作，于是我们就使用了这样一个线程池：

```java
/** 队列信息 */
private ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(8);

/** 线程池信息 */
private ThreadPoolExecutor threadPool =
      new ThreadPoolExecutor(
          2,
          4,
          30,
          TimeUnit.SECONDS,
          queue,
          new TaskThreadFactory(),
          new ThreadPoolExecutor.CallerRunsPolicy());
```

然后向线程池中提交异步计算任务

```java
  @RequestMapping(
      value = "/maxThread",
      method = {RequestMethod.GET, RequestMethod.POST})
  public int mixThread() throws ExecutionException, InterruptedException {
    return threadPool
        .submit(
            () -> {
              try {
                Thread.sleep(20);
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
              return 1;
            })
        .get();
  }
```



但是，注意但是来了，在我不知情况下，这个线程池，被我们组的人给复用了，他后来回忆说，他当时想的是，既然已经存在一个线程池了，我就直接用好了，为什么要自己创建呢！

然后他向线程池提交的是一个IO密集型任务。

```java
  /** 最大深度 */
  private static final int MAX_DEEP = 204800;

  @PostConstruct
  public void init() {
    ThreadPrint.printStatus(threadPool);

    new Thread(
            () -> {
              // 向线程池中提交IO密集型任务
              threadPool.submit(
                  () -> {
                    while (true) {
                      fileReader(new File("d:"), 0);
                      try {
                        Thread.sleep(100);
                      } catch (InterruptedException e) {
                        e.printStackTrace();
                      }
                    }
                  });
            })
        .start();
  }

  /**
   * 使用文件读取模拟IO的压力
   *
   * @param file
   * @param max
   */
  private void fileReader(File file, int max) {
    if (max > MAX_DEEP) {
      return;
    }
    if (file.isFile()) {
      return;
    }
    // 文件夹遍历读取
    else if (file.isDirectory()) {
      for (File item : file.listFiles()) {
        this.fileReader(item, max + 1);
      }
    }
  }
```



经过简单的压测发现性能：

```sh
[root@standalone wrk]# wrk -t 10 -c 100 -d 20s http://192.168.16.151:8080/demo/maxThread
Running 20s test @ http://192.168.16.151:8080/demo/maxThread
  10 threads and 100 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     1.00s   574.15ms   1.99s    58.76%
    Req/Sec    21.08     18.14    50.00     47.92%
  968 requests in 20.06s, 131.40KB read
  Socket errors: connect 0, read 0, write 0, timeout 871
Requests/sec:     48.27
Transfer/sec:      6.55KB
[root@standalone wrk]# 

```

TPS只有48，性能非常的差。



原因分析：

由于线程池使用的拒绝策略是：CallerRunsPolicy，这个策略的意思就是在当前线程执行异步计算，当线程池处于饱和状态时，计算任务就会在tomcat的核心线程中执行，这就会影响到tomcat的正常同步处理的线程，将使用整个线程池的效率低下，甚至可能会造成整个应用响应的崩溃，由于tomcat的线程池也是采用无界队列。



### 解决方案:

解决这个问题还是比较容易的，将IO密集型的任务放到其他线程池中处理。将IO密集型与计算型任务分离，两个互不影响。我们来看下改靠后的代码。

使用独立的线程来进行“计算”任务“，这个计算任务是使用休眠来模拟的，这其实不是CPU密集型任务。这类任务线程池太小会限制吞吐能力。

```java
@RestController
@RequestMapping("/demo")
public class MixThreadPoolFacade {

  /** CPU密集型 */
  private ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(1000);

  /** 线程池信息 */
  private ThreadPoolExecutor threadPool =
      new ThreadPoolExecutor(
          32,
          64,
          30,
          TimeUnit.SECONDS,
          queue,
          new TaskThreadFactory(),
          new ThreadPoolExecutor.CallerRunsPolicy());


    

    
  @RequestMapping(
      value = "/maxThread",
      method = {RequestMethod.GET, RequestMethod.POST})
  public int mixThread() throws ExecutionException, InterruptedException {

    return threadPool
        .submit(
            () -> {
              try {
                Thread.sleep(20);
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
              return 1;
            })
        .get();
  }    


```



我们再来进行一次简单的压测:

```sh
[root@standalone wrk]# wrk -t 10 -c 100 -d 20s http://192.168.16.151:8080/demo/maxThread
Running 20s test @ http://192.168.16.151:8080/demo/maxThread
  10 threads and 100 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    67.77ms   23.43ms 373.96ms   98.14%
    Req/Sec   153.21     19.49   200.00     74.97%
  30221 requests in 20.10s, 4.01MB read
Requests/sec:   1503.62
Transfer/sec:    204.39KB
```



现在的TPS能达到1502,提升了近30倍。

这种问题经常是在测试的时候都是好好的，都是在上线以后才发现，因为一压测都是单接口。像这种两个接口混用线程池的，查找这个问题的过程也是前前后后定位了好长时间，这个问题的定位也是通过抓取线程栈，才找到线程混用的问题。





## 默认线程池的行为分析:

还是由代码入手吧：

这是一段测试程序，测试线程的默认行为。

自定义一个线程池，线程池的核心大小为2，最大线程数为4，队列大小为8，采用的是AbortPolicy的拒绝策略，也就是当任务添加到线程池失败会报出:RejectedExecutionException,自定义了一个线程工厂类，为线程中的线程添加一个有含义的线程的名称。

再写一段程序来测试观察下线程池。测试的逻辑为每隔一秒向线程池中添加一个任务，循环20次，每个任务需要10秒才能这完成：

```java
  @Test
  public void TestDefaultThreadPool() {
    ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(8);

    ThreadPoolExecutor threadPool =
        new ThreadPoolExecutor(
            2,
            4,
            30,
            TimeUnit.SECONDS,
            queue,
            new TaskThreadFactory("test-thread-pool"),
            new ThreadPoolExecutor.AbortPolicy());

    // 启动线程池的信息输出
    ThreadPrint.printStatusTimeOut(threadPool);

    // 使用一个计数器跟踪完成的任务数
    AtomicInteger atomicInteger = new AtomicInteger();

    int maxDa = 20;

    // 每秒种向线程池中提交一个任务，一共提交20次,任务执行时间10秒，
    List<Future> dataRsp = new ArrayList<>((int) (maxDa / 0.75f + 1));
    for (int i = 0; i < maxDa; i++) {
      final int itemValue = i;
      try {
        dataRsp.add(
            threadPool.submit(
                () -> {
                  // 进行变量的增长
                  atomicInteger.incrementAndGet();
                  try {
                    Thread.sleep(10000L);
                  } catch (InterruptedException e) {
                    e.printStackTrace();
                  }
                  return itemValue;
                }));

        Thread.sleep(1000L);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    for (Future dataRspItem : dataRsp) {
      try {
        dataRspItem.get();
      } catch (InterruptedException e) {
        e.printStackTrace();
      } catch (ExecutionException e) {
        e.printStackTrace();
      }
    }

    try {
      Thread.sleep(45000L);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    System.out.println(" data Value :" + atomicInteger.get());

    threadPool.shutdown();
  }




public class TaskThreadFactory implements ThreadFactory {

  private static final String DEF_PREFIX = "task-thread-";

  private final String prefixName;

  public TaskThreadFactory() {
    this.prefixName = DEF_PREFIX;
  }

  public TaskThreadFactory(String prefixName) {
    this.prefixName = prefixName;
  }

  @Override
  public Thread newThread(Runnable r) {
    // 仅设置一个线程名称
    Thread currThread = new Thread(r, prefixName);
    return currThread;
  }
}
```



任务在执行1分30秒后，输出结果：14，有6次提交失败了。

![](D:\doc\博客\数据结构与算法\多线程\线程池的坑\线程池的默认行为分析-输出.png)

在控制台中也输出了6个错误信息

```java
java.util.concurrent.RejectedExecutionException: Task java.util.concurrent.FutureTask@327471b5 rejected from java.util.concurrent.ThreadPoolExecutor@4157f54e[Running, pool size = 4, active threads = 4, queued tasks = 8, completed tasks = 2]
	at java.util.concurrent.ThreadPoolExecutor$AbortPolicy.rejectedExecution(ThreadPoolExecutor.java:2063)
	at java.util.concurrent.ThreadPoolExecutor.reject(ThreadPoolExecutor.java:830)
	at java.util.concurrent.ThreadPoolExecutor.execute(ThreadPoolExecutor.java:1379)
	at java.util.concurrent.AbstractExecutorService.submit(AbstractExecutorService.java:134)
	at com.liujun.thread.threadpool.TestTaskThreadDataPoolDefault.TestDefaultThreadPool(TestTaskThreadDataPoolDefault.java:72)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50)
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47)
	at mockit.integration.junit4.internal.JUnit4TestRunnerDecorator.executeTestMethod(JUnit4TestRunnerDecorator.java:162)
	at mockit.integration.junit4.internal.JUnit4TestRunnerDecorator.invokeExplosively(JUnit4TestRunnerDecorator.java:71)
	at mockit.integration.junit4.internal.MockFrameworkMethod.invokeExplosively(MockFrameworkMethod.java:37)
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java)
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:78)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:57)
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290)
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71)
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288)
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58)
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268)
	at org.junit.runners.ParentRunner.run(ParentRunner.java:363)
	at org.junit.runner.JUnitCore.run(JUnitCore.java:137)
	at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68)
	at com.intellij.rt.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:33)
	at com.intellij.rt.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:230)
	at com.intellij.rt.junit.JUnitStarter.main(JUnitStarter.java:58)
java.util.concurrent.RejectedExecutionException: Task java.util.concurrent.FutureTask@47f6473 rejected from java.util.concurrent.ThreadPoolExecutor@4157f54e[Running, pool size = 4, active threads = 4, queued tasks = 8, completed tasks = 2]
	at java.util.concurrent.ThreadPoolExecutor$AbortPolicy.rejectedExecution(ThreadPoolExecutor.java:2063)
	at java.util.concurrent.ThreadPoolExecutor.reject(ThreadPoolExecutor.java:830)
	at java.util.concurrent.ThreadPoolExecutor.execute(ThreadPoolExecutor.java:1379)
	at java.util.concurrent.AbstractExecutorService.submit(AbstractExecutorService.java:134)
	at com.liujun.thread.threadpool.TestTaskThreadDataPoolDefault.TestDefaultThreadPool(TestTaskThreadDataPoolDefault.java:72)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50)
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47)
	at mockit.integration.junit4.internal.JUnit4TestRunnerDecorator.executeTestMethod(JUnit4TestRunnerDecorator.java:162)
	at mockit.integration.junit4.internal.JUnit4TestRunnerDecorator.invokeExplosively(JUnit4TestRunnerDecorator.java:71)
	at mockit.integration.junit4.internal.MockFrameworkMethod.invokeExplosively(MockFrameworkMethod.java:37)
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java)
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:78)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:57)
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290)
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71)
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288)
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58)
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268)
	at org.junit.runners.ParentRunner.run(ParentRunner.java:363)
	at org.junit.runner.JUnitCore.run(JUnitCore.java:137)
	at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68)
	at com.intellij.rt.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:33)
	at com.intellij.rt.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:230)
	at com.intellij.rt.junit.JUnitStarter.main(JUnitStarter.java:58)
java.util.concurrent.RejectedExecutionException: Task java.util.concurrent.FutureTask@6b143ee9 rejected from java.util.concurrent.ThreadPoolExecutor@4157f54e[Running, pool size = 4, active threads = 4, queued tasks = 8, completed tasks = 2]
	at java.util.concurrent.ThreadPoolExecutor$AbortPolicy.rejectedExecution(ThreadPoolExecutor.java:2063)
	at java.util.concurrent.ThreadPoolExecutor.reject(ThreadPoolExecutor.java:830)
	at java.util.concurrent.ThreadPoolExecutor.execute(ThreadPoolExecutor.java:1379)
	at java.util.concurrent.AbstractExecutorService.submit(AbstractExecutorService.java:134)
	at com.liujun.thread.threadpool.TestTaskThreadDataPoolDefault.TestDefaultThreadPool(TestTaskThreadDataPoolDefault.java:72)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50)
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47)
	at mockit.integration.junit4.internal.JUnit4TestRunnerDecorator.executeTestMethod(JUnit4TestRunnerDecorator.java:162)
	at mockit.integration.junit4.internal.JUnit4TestRunnerDecorator.invokeExplosively(JUnit4TestRunnerDecorator.java:71)
	at mockit.integration.junit4.internal.MockFrameworkMethod.invokeExplosively(MockFrameworkMethod.java:37)
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java)
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:78)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:57)
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290)
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71)
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288)
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58)
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268)
	at org.junit.runners.ParentRunner.run(ParentRunner.java:363)
	at org.junit.runner.JUnitCore.run(JUnitCore.java:137)
	at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68)
	at com.intellij.rt.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:33)
	at com.intellij.rt.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:230)
	at com.intellij.rt.junit.JUnitStarter.main(JUnitStarter.java:58)
java.util.concurrent.RejectedExecutionException: Task java.util.concurrent.FutureTask@6615435c rejected from java.util.concurrent.ThreadPoolExecutor@4157f54e[Running, pool size = 4, active threads = 4, queued tasks = 8, completed tasks = 2]
	at java.util.concurrent.ThreadPoolExecutor$AbortPolicy.rejectedExecution(ThreadPoolExecutor.java:2063)
	at java.util.concurrent.ThreadPoolExecutor.reject(ThreadPoolExecutor.java:830)
	at java.util.concurrent.ThreadPoolExecutor.execute(ThreadPoolExecutor.java:1379)
	at java.util.concurrent.AbstractExecutorService.submit(AbstractExecutorService.java:134)
	at com.liujun.thread.threadpool.TestTaskThreadDataPoolDefault.TestDefaultThreadPool(TestTaskThreadDataPoolDefault.java:72)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50)
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47)
	at mockit.integration.junit4.internal.JUnit4TestRunnerDecorator.executeTestMethod(JUnit4TestRunnerDecorator.java:162)
	at mockit.integration.junit4.internal.JUnit4TestRunnerDecorator.invokeExplosively(JUnit4TestRunnerDecorator.java:71)
	at mockit.integration.junit4.internal.MockFrameworkMethod.invokeExplosively(MockFrameworkMethod.java:37)
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java)
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:78)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:57)
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290)
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71)
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288)
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58)
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268)
	at org.junit.runners.ParentRunner.run(ParentRunner.java:363)
	at org.junit.runner.JUnitCore.run(JUnitCore.java:137)
	at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68)
	at com.intellij.rt.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:33)
	at com.intellij.rt.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:230)
	at com.intellij.rt.junit.JUnitStarter.main(JUnitStarter.java:58)
java.util.concurrent.RejectedExecutionException: Task java.util.concurrent.FutureTask@3a03464 rejected from java.util.concurrent.ThreadPoolExecutor@4157f54e[Running, pool size = 4, active threads = 4, queued tasks = 8, completed tasks = 2]
	at java.util.concurrent.ThreadPoolExecutor$AbortPolicy.rejectedExecution(ThreadPoolExecutor.java:2063)
	at java.util.concurrent.ThreadPoolExecutor.reject(ThreadPoolExecutor.java:830)
	at java.util.concurrent.ThreadPoolExecutor.execute(ThreadPoolExecutor.java:1379)
	at java.util.concurrent.AbstractExecutorService.submit(AbstractExecutorService.java:134)
	at com.liujun.thread.threadpool.TestTaskThreadDataPoolDefault.TestDefaultThreadPool(TestTaskThreadDataPoolDefault.java:72)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50)
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47)
	at mockit.integration.junit4.internal.JUnit4TestRunnerDecorator.executeTestMethod(JUnit4TestRunnerDecorator.java:162)
	at mockit.integration.junit4.internal.JUnit4TestRunnerDecorator.invokeExplosively(JUnit4TestRunnerDecorator.java:71)
	at mockit.integration.junit4.internal.MockFrameworkMethod.invokeExplosively(MockFrameworkMethod.java:37)
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java)
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:78)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:57)
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290)
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71)
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288)
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58)
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268)
	at org.junit.runners.ParentRunner.run(ParentRunner.java:363)
	at org.junit.runner.JUnitCore.run(JUnitCore.java:137)
	at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68)
	at com.intellij.rt.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:33)
	at com.intellij.rt.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:230)
	at com.intellij.rt.junit.JUnitStarter.main(JUnitStarter.java:58)
java.util.concurrent.RejectedExecutionException: Task java.util.concurrent.FutureTask@617c74e5 rejected from java.util.concurrent.ThreadPoolExecutor@4157f54e[Running, pool size = 4, active threads = 4, queued tasks = 8, completed tasks = 2]
	at java.util.concurrent.ThreadPoolExecutor$AbortPolicy.rejectedExecution(ThreadPoolExecutor.java:2063)
	at java.util.concurrent.ThreadPoolExecutor.reject(ThreadPoolExecutor.java:830)
	at java.util.concurrent.ThreadPoolExecutor.execute(ThreadPoolExecutor.java:1379)
	at java.util.concurrent.AbstractExecutorService.submit(AbstractExecutorService.java:134)
	at com.liujun.thread.threadpool.TestTaskThreadDataPoolDefault.TestDefaultThreadPool(TestTaskThreadDataPoolDefault.java:72)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50)
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47)
	at mockit.integration.junit4.internal.JUnit4TestRunnerDecorator.executeTestMethod(JUnit4TestRunnerDecorator.java:162)
	at mockit.integration.junit4.internal.JUnit4TestRunnerDecorator.invokeExplosively(JUnit4TestRunnerDecorator.java:71)
	at mockit.integration.junit4.internal.MockFrameworkMethod.invokeExplosively(MockFrameworkMethod.java:37)
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java)
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:78)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:57)
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290)
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71)
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288)
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58)
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268)
	at org.junit.runners.ParentRunner.run(ParentRunner.java:363)
	at org.junit.runner.JUnitCore.run(JUnitCore.java:137)
	at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68)
	at com.intellij.rt.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:33)
	at com.intellij.rt.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:230)
	at com.intellij.rt.junit.JUnitStarter.main(JUnitStarter.java:58)
```



把数据绘制成图表:

![](D:\doc\博客\数据结构与算法\多线程\线程池的坑\线程池的默认行为分析图表.png)



### 线程池默认的工作行为:

1. **不会初始化corePoolSize个线程，有任务来了才创建工作线程。**
2. **当核心线程满了之后，不会立即扩容线程池，而是把任务加入到工作队列中。**
3. **当工作队列满了之后才扩容线程池，一直到线程的个数达到maximumPoolSize为止。**
4. **如果队列已满且达到了最大线程后还有任务进来，按照拒绝策略处理；**
5. **当线程数大于核心线程数时，线程等待KeepAliveTime后还是没有任务需要处理，则收缩线程数到核心线程数。**





如果用图来表示就是:

![](D:\doc\博客\数据结构与算法\多线程\线程池的坑\默认线程池的行为.png)







## 以响应优先的线程池分析

这是线程池提供的默认线程池的处理策略，针对一些常规的任务来说没有什么问题，比如定时任务啊，后台计算啊，这类任务并不关心任务的先后顺序，只要执行了即可，但还有一些任务就不是这样子，比如用户的响应，那是不能采用这种工作行为的，可以脑补下，当100个用户同时访问网站，我们限制了核心线程数为20,最大线程数为40，队列为100，前20个用户请求将被首先放入到线程池的核心线程中执行，还剩余80个用户请求。这时候所有的用户请求将都被放入到队列中。明明还空余那个线程。却不能提供服务，所以在这种以响应优先的场景下，是不能采用这种默认的线程池策略的。

那具体怎么做呢？

```java
public class TaskThreadDataPoolGood {

  /** 实例信息 */
  public static final TaskThreadDataPoolGood INSTANCE = new TaskThreadDataPoolGood();

  public TaskThreadDataPoolGood() {
    init();
  }

  /** 最大队列长度 */
  private static final int MAX_SIZE = 8;

  /** 队列信息 */
  private BlockingQueue<Runnable> QUEUE =
      new LinkedTransferQueue<Runnable>() {
        @Override
        public boolean offer(Runnable e) {
          // 如果存在一个消费者已经等待接收它，则立即传送指定的元素，否则返回false，并且不进入队列。
          return tryTransfer(e);
        }
      };

  /** 最小核心线程数 */
  private static final int CORE_SIZE = 2;

  /** 最大的线程数 */
  private static final int MAX_POOL_SIZE = 4;

  /** 最大的保持的时间 */
  private static final int KEEP_ALIVE_TIME = 30;

  /** 线程池信息 */
  private ThreadPoolExecutor THREAD_POOL =
      new ThreadPoolExecutor(CORE_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, QUEUE);

  public void init() {
    // 设置拒绝策略
    THREAD_POOL.setRejectedExecutionHandler(
        new RejectedExecutionHandler() {
          @Override
          public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            // 当超过设置的最大队列大小时，执行拒绝策略
            if (executor.getQueue().size() >= MAX_SIZE) {
              throw new RejectedExecutionException(
                  "Task " + r.toString() + " rejected from " + executor.toString());
            }
            // 如果未到最大队列大小，则执行向队列中添加数据
            else {
              try {
                executor.getQueue().put(r);
              } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
              }
            }
          }
        });
  }

  /**
   * 最大的任务数
   *
   * @return
   */
  public int maxData() {
    return MAX_SIZE + MAX_POOL_SIZE;
  }

  /**
   * 获取线程池
   *
   * @return 当前的线程池信息
   */
  public ThreadPoolExecutor getThreadPool() {
    return THREAD_POOL;
  }

  /**
   * 提交任务至线程池
   *
   * @param dataRun 任务
   */
  public Future submit(Runnable dataRun) {
    return THREAD_POOL.submit(dataRun);
  }

  /**
   * 提交任务至线程池
   *
   * @param dataRun 任务
   */
  public Future submit(Callable dataRun) {
    return THREAD_POOL.submit(dataRun);
  }
}
```

要解释这个改靠，还得先从线程池的核心方法上说起:

```java
    public void execute(Runnable command) {
        if (command == null)
            throw new NullPointerException();
        /*
         * Proceed in 3 steps:
         *
         * 1. If fewer than corePoolSize threads are running, try to
         * start a new thread with the given command as its first
         * task.  The call to addWorker atomically checks runState and
         * workerCount, and so prevents false alarms that would add
         * threads when it shouldn't, by returning false.
         *
         * 2. If a task can be successfully queued, then we still need
         * to double-check whether we should have added a thread
         * (because existing ones died since last checking) or that
         * the pool shut down since entry into this method. So we
         * recheck state and if necessary roll back the enqueuing if
         * stopped, or start a new thread if there are none.
         *
         * 3. If we cannot queue task, then we try to add a new
         * thread.  If it fails, we know we are shut down or saturated
         * and so reject the task.
         */
        int c = ctl.get();
        //如果队列小于核心线程数，则直接添加线程
        if (workerCountOf(c) < corePoolSize) {
            if (addWorker(command, true))
                return;
            c = ctl.get();
        }
        //检查状态，将指定元素添加到此列表的末尾
        if (isRunning(c) && workQueue.offer(command)) {
            int recheck = ctl.get();
            //检查运行状态，在宕机时为true
            if (! isRunning(recheck) && remove(command))
                reject(command);
            //检查队列是否已经已满的检查，如果队列满了，添加线程，直到最大线程数
            else if (workerCountOf(recheck) == 0)
                addWorker(null, false);
        }
        //队列添加至末尾失败，说明队列已满，并且添加最大线程也失败说明已经到达最大线程数，两个都到达，执行拒绝策略。
        else if (!addWorker(command, false))
            reject(command);
    }
```



先解释第一个问题吧

**如何解决优先扩容线程?**

当核心线程已经满了，这时候再来线程就要加入到队列中，但是这时候调用的时候队列的offer方法，这里重写了offer方法，调用了tryTransfer.

先下这个tryTransfer方法的解释吧。

>存在一个消费者已经等待接收它，则立即传送指定的元素，否则返回false，并且不进入队列。

当前很状态很明显么，只有两个核心状态被启动了。不存在其他线程，调用这个方法会返回false。加入队列末尾方法offer未满足，则执行以下代码:

```java
        else if (!addWorker(command, false))
```

此时添加任务线程，会不会成功呢？因为当前仅启动了核心线程，还没有到达最大线程数，所有添加线程是成功的。就这样做到了优先执行了扩容，然后再执行加入到队列中。是不是非常的巧妙呢!



**那拒绝策略怎么办呢？**

先解释下为什么需要重写拒绝策？

当线程池扩容完成后，接下来再添加任务，就会执行 reject(command);而此时还仅完成了线程扩容到最大线程数，队列还是空的，所以必须要重写拒绝策略，让任务进入缓冲队列，以让线程并行执行。而超过队列的最大值后，这里就应该执行拒绝策略了，因为线程已经 扩容到最大，队列也满了，就可以真正的执行拒绝策略了。



先来看看这段代码实现:

```java
// 设置拒绝策略
    THREAD_POOL.setRejectedExecutionHandler(
        new RejectedExecutionHandler() {
          @Override
          public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            // 当超过设置的最大队列大小时，执行拒绝策略
            if (executor.getQueue().size() >= MAX_SIZE) {
              throw new RejectedExecutionException(
                  "Task " + r.toString() + " rejected from " + executor.toString());
            }
            // 如果未到最大队列大小，则执行向队列中添加数据
            else {
              try {
                executor.getQueue().put(r);
              } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
              }
            }
          }
        });
```



再以分析默认线程池行为的代码再来分析一遍这个激进版的线程池。

来看代码:

```java
  @Test
  public void TestDefaultThreadPool() {
    ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(8);

    ThreadPoolExecutor threadPool =
        new ThreadPoolExecutor(
            2,
            4,
            30,
            TimeUnit.SECONDS,
            queue,
            new TaskThreadFactory("test-thread-pool"),
            new ThreadPoolExecutor.AbortPolicy());

    // 启动线程池的信息输出
    ThreadPrint.printStatusTimeOut(threadPool);

    // 使用一个计数器跟踪完成的任务数
    AtomicInteger atomicInteger = new AtomicInteger();

    int maxDa = 20;

    // 每秒种向线程池中提交一个任务，一共提交20次,任务执行时间10秒，
    List<Future> dataRsp = new ArrayList<>((int) (maxDa / 0.75f + 1));
    for (int i = 0; i < maxDa; i++) {
      final int itemValue = i;
      try {
        dataRsp.add(
            threadPool.submit(
                () -> {
                  // 进行变量的增长
                  atomicInteger.incrementAndGet();
                  try {
                    Thread.sleep(10000L);
                  } catch (InterruptedException e) {
                    e.printStackTrace();
                  }
                  return itemValue;
                }));

        Thread.sleep(1000L);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    for (Future dataRspItem : dataRsp) {
      try {
        dataRspItem.get();
      } catch (InterruptedException e) {
        e.printStackTrace();
      } catch (ExecutionException e) {
        e.printStackTrace();
      }
    }

    try {
      Thread.sleep(45000L);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    System.out.println(" data Value :" + atomicInteger.get());

    threadPool.shutdown();
  }
```



最后执行的结果是多少呢?

![](D:\doc\博客\数据结构与算法\多线程\线程池的坑\线程池的默认行为分析-激进-输出.png)

控制台输出的拒绝错误的信息

```java
java.util.concurrent.RejectedExecutionException: Task java.util.concurrent.FutureTask@327471b5 rejected from java.util.concurrent.ThreadPoolExecutor@4157f54e[Running, pool size = 4, active threads = 4, queued tasks = 8, completed tasks = 2]
	at java.util.concurrent.ThreadPoolExecutor$AbortPolicy.rejectedExecution(ThreadPoolExecutor.java:2063)
	at java.util.concurrent.ThreadPoolExecutor.reject(ThreadPoolExecutor.java:830)
	at java.util.concurrent.ThreadPoolExecutor.execute(ThreadPoolExecutor.java:1379)
	at java.util.concurrent.AbstractExecutorService.submit(AbstractExecutorService.java:134)
	at com.liujun.thread.threadpool.TestTaskThreadDataPoolDefault.TestDefaultThreadPool(TestTaskThreadDataPoolDefault.java:72)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50)
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47)
	at mockit.integration.junit4.internal.JUnit4TestRunnerDecorator.executeTestMethod(JUnit4TestRunnerDecorator.java:162)
	at mockit.integration.junit4.internal.JUnit4TestRunnerDecorator.invokeExplosively(JUnit4TestRunnerDecorator.java:71)
	at mockit.integration.junit4.internal.MockFrameworkMethod.invokeExplosively(MockFrameworkMethod.java:37)
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java)
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:78)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:57)
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290)
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71)
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288)
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58)
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268)
	at org.junit.runners.ParentRunner.run(ParentRunner.java:363)
	at org.junit.runner.JUnitCore.run(JUnitCore.java:137)
	at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68)
	at com.intellij.rt.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:33)
	at com.intellij.rt.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:230)
	at com.intellij.rt.junit.JUnitStarter.main(JUnitStarter.java:58)
java.util.concurrent.RejectedExecutionException: Task java.util.concurrent.FutureTask@47f6473 rejected from java.util.concurrent.ThreadPoolExecutor@4157f54e[Running, pool size = 4, active threads = 4, queued tasks = 8, completed tasks = 2]
	at java.util.concurrent.ThreadPoolExecutor$AbortPolicy.rejectedExecution(ThreadPoolExecutor.java:2063)
	at java.util.concurrent.ThreadPoolExecutor.reject(ThreadPoolExecutor.java:830)
	at java.util.concurrent.ThreadPoolExecutor.execute(ThreadPoolExecutor.java:1379)
	at java.util.concurrent.AbstractExecutorService.submit(AbstractExecutorService.java:134)
	at com.liujun.thread.threadpool.TestTaskThreadDataPoolDefault.TestDefaultThreadPool(TestTaskThreadDataPoolDefault.java:72)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50)
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47)
	at mockit.integration.junit4.internal.JUnit4TestRunnerDecorator.executeTestMethod(JUnit4TestRunnerDecorator.java:162)
	at mockit.integration.junit4.internal.JUnit4TestRunnerDecorator.invokeExplosively(JUnit4TestRunnerDecorator.java:71)
	at mockit.integration.junit4.internal.MockFrameworkMethod.invokeExplosively(MockFrameworkMethod.java:37)
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java)
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:78)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:57)
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290)
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71)
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288)
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58)
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268)
	at org.junit.runners.ParentRunner.run(ParentRunner.java:363)
	at org.junit.runner.JUnitCore.run(JUnitCore.java:137)
	at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68)
	at com.intellij.rt.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:33)
	at com.intellij.rt.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:230)
	at com.intellij.rt.junit.JUnitStarter.main(JUnitStarter.java:58)
java.util.concurrent.RejectedExecutionException: Task java.util.concurrent.FutureTask@6b143ee9 rejected from java.util.concurrent.ThreadPoolExecutor@4157f54e[Running, pool size = 4, active threads = 4, queued tasks = 8, completed tasks = 2]
	at java.util.concurrent.ThreadPoolExecutor$AbortPolicy.rejectedExecution(ThreadPoolExecutor.java:2063)
	at java.util.concurrent.ThreadPoolExecutor.reject(ThreadPoolExecutor.java:830)
	at java.util.concurrent.ThreadPoolExecutor.execute(ThreadPoolExecutor.java:1379)
	at java.util.concurrent.AbstractExecutorService.submit(AbstractExecutorService.java:134)
	at com.liujun.thread.threadpool.TestTaskThreadDataPoolDefault.TestDefaultThreadPool(TestTaskThreadDataPoolDefault.java:72)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50)
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47)
	at mockit.integration.junit4.internal.JUnit4TestRunnerDecorator.executeTestMethod(JUnit4TestRunnerDecorator.java:162)
	at mockit.integration.junit4.internal.JUnit4TestRunnerDecorator.invokeExplosively(JUnit4TestRunnerDecorator.java:71)
	at mockit.integration.junit4.internal.MockFrameworkMethod.invokeExplosively(MockFrameworkMethod.java:37)
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java)
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:78)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:57)
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290)
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71)
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288)
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58)
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268)
	at org.junit.runners.ParentRunner.run(ParentRunner.java:363)
	at org.junit.runner.JUnitCore.run(JUnitCore.java:137)
	at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68)
	at com.intellij.rt.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:33)
	at com.intellij.rt.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:230)
	at com.intellij.rt.junit.JUnitStarter.main(JUnitStarter.java:58)
java.util.concurrent.RejectedExecutionException: Task java.util.concurrent.FutureTask@6615435c rejected from java.util.concurrent.ThreadPoolExecutor@4157f54e[Running, pool size = 4, active threads = 4, queued tasks = 8, completed tasks = 2]
	at java.util.concurrent.ThreadPoolExecutor$AbortPolicy.rejectedExecution(ThreadPoolExecutor.java:2063)
	at java.util.concurrent.ThreadPoolExecutor.reject(ThreadPoolExecutor.java:830)
	at java.util.concurrent.ThreadPoolExecutor.execute(ThreadPoolExecutor.java:1379)
	at java.util.concurrent.AbstractExecutorService.submit(AbstractExecutorService.java:134)
	at com.liujun.thread.threadpool.TestTaskThreadDataPoolDefault.TestDefaultThreadPool(TestTaskThreadDataPoolDefault.java:72)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50)
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47)
	at mockit.integration.junit4.internal.JUnit4TestRunnerDecorator.executeTestMethod(JUnit4TestRunnerDecorator.java:162)
	at mockit.integration.junit4.internal.JUnit4TestRunnerDecorator.invokeExplosively(JUnit4TestRunnerDecorator.java:71)
	at mockit.integration.junit4.internal.MockFrameworkMethod.invokeExplosively(MockFrameworkMethod.java:37)
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java)
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:78)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:57)
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290)
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71)
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288)
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58)
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268)
	at org.junit.runners.ParentRunner.run(ParentRunner.java:363)
	at org.junit.runner.JUnitCore.run(JUnitCore.java:137)
	at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68)
	at com.intellij.rt.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:33)
	at com.intellij.rt.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:230)
	at com.intellij.rt.junit.JUnitStarter.main(JUnitStarter.java:58)
java.util.concurrent.RejectedExecutionException: Task java.util.concurrent.FutureTask@3a03464 rejected from java.util.concurrent.ThreadPoolExecutor@4157f54e[Running, pool size = 4, active threads = 4, queued tasks = 8, completed tasks = 2]
	at java.util.concurrent.ThreadPoolExecutor$AbortPolicy.rejectedExecution(ThreadPoolExecutor.java:2063)
	at java.util.concurrent.ThreadPoolExecutor.reject(ThreadPoolExecutor.java:830)
	at java.util.concurrent.ThreadPoolExecutor.execute(ThreadPoolExecutor.java:1379)
	at java.util.concurrent.AbstractExecutorService.submit(AbstractExecutorService.java:134)
	at com.liujun.thread.threadpool.TestTaskThreadDataPoolDefault.TestDefaultThreadPool(TestTaskThreadDataPoolDefault.java:72)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50)
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47)
	at mockit.integration.junit4.internal.JUnit4TestRunnerDecorator.executeTestMethod(JUnit4TestRunnerDecorator.java:162)
	at mockit.integration.junit4.internal.JUnit4TestRunnerDecorator.invokeExplosively(JUnit4TestRunnerDecorator.java:71)
	at mockit.integration.junit4.internal.MockFrameworkMethod.invokeExplosively(MockFrameworkMethod.java:37)
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java)
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:78)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:57)
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290)
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71)
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288)
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58)
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268)
	at org.junit.runners.ParentRunner.run(ParentRunner.java:363)
	at org.junit.runner.JUnitCore.run(JUnitCore.java:137)
	at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68)
	at com.intellij.rt.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:33)
	at com.intellij.rt.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:230)
	at com.intellij.rt.junit.JUnitStarter.main(JUnitStarter.java:58)
java.util.concurrent.RejectedExecutionException: Task java.util.concurrent.FutureTask@617c74e5 rejected from java.util.concurrent.ThreadPoolExecutor@4157f54e[Running, pool size = 4, active threads = 4, queued tasks = 8, completed tasks = 2]
	at java.util.concurrent.ThreadPoolExecutor$AbortPolicy.rejectedExecution(ThreadPoolExecutor.java:2063)
	at java.util.concurrent.ThreadPoolExecutor.reject(ThreadPoolExecutor.java:830)
	at java.util.concurrent.ThreadPoolExecutor.execute(ThreadPoolExecutor.java:1379)
	at java.util.concurrent.AbstractExecutorService.submit(AbstractExecutorService.java:134)
	at com.liujun.thread.threadpool.TestTaskThreadDataPoolDefault.TestDefaultThreadPool(TestTaskThreadDataPoolDefault.java:72)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50)
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47)
	at mockit.integration.junit4.internal.JUnit4TestRunnerDecorator.executeTestMethod(JUnit4TestRunnerDecorator.java:162)
	at mockit.integration.junit4.internal.JUnit4TestRunnerDecorator.invokeExplosively(JUnit4TestRunnerDecorator.java:71)
	at mockit.integration.junit4.internal.MockFrameworkMethod.invokeExplosively(MockFrameworkMethod.java:37)
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java)
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:78)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:57)
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290)
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71)
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288)
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58)
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268)
	at org.junit.runners.ParentRunner.run(ParentRunner.java:363)
	at org.junit.runner.JUnitCore.run(JUnitCore.java:137)
	at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68)
	at com.intellij.rt.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:33)
	at com.intellij.rt.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:230)
	at com.intellij.rt.junit.JUnitStarter.main(JUnitStarter.java:58)

```





然后再以图表形式来分析下策略吧

![](D:\doc\博客\数据结构与算法\多线程\线程池的坑\线程池的默认行为分析图表-激进.png)

**激进线程池的默认策略:**

1. **不会初始化corePoolSize个线程，有任务来了才创建工作线程。**
2. **当核心线程满了之后，立即扩容线程池，一直到线程的个数达到maximumPoolSize为止。**
3. **当线程池中达到最大线程数后，会将任务加入到队列中。**
4. **如果到达了最大线程后队列已满还有任务进来，按照拒绝策略处理。**
5. **当线程数大于核心线程数时，线程等待KeepAliveTime后还是没有任务需要处理，则收缩线程数到核心线程数。**



激动版本的线程池的策略:

![](D:\doc\博客\数据结构与算法\多线程\线程池的坑\激动线程池的行为.png)









## 最佳实践：

### 一定要给线程设置一个有意义的名称。

此为必须，一定要注意，此举的意义在于在出现问题时，一个有意义的线程名称，能够方便的找出出现问题的线程池。

可使用guava库

```java
  /** 线程工厂 */
  private ThreadFactory threadFactory =
      new ThreadFactoryBuilder().setNameFormat("threadNamePrefix-%d").setDaemon(true).build();

  /** 线程池 */
  private ThreadPoolExecutor threadPoolData =
      new ThreadPoolExecutor(
          CORE_SIZE,
          MAX_POOL_SIZE,
          KEEP_ALIVE_TIME,
          TimeUnit.SECONDS,
          queue,
          threadFactory,
          new ThreadPoolExecutor.CallerRunsPolicy());
```

自定义的方式：

```java
/**
 * 给线程池中的线程设置一个有意义的名称，用于在dump线程栈后，可以很方便的对问题进行排查
 *
 */
public class TaskThreadFactory implements ThreadFactory {

  /** 编号 */
  private final AtomicInteger threadNum = new AtomicInteger(0);

  /** 名称 */
  private final String name;

  public TaskThreadFactory(String name) {
    this.name = name;
  }

  @Override
  public Thread newThread(@NotNull Runnable r) {
    Thread t = new Thread(r);
    t.setName(name + "-" + threadNum.incrementAndGet());
    return t;
  }
}



  /** 线程池创建 */
  private ThreadPoolExecutor threadPool =
      new ThreadPoolExecutor(
          CORE_SIZE,
          MAX_POOL_SIZE,
          KEEP_ALIVE_TIME,
          TimeUnit.SECONDS,
          queue,
          new TaskThreadFactory("dataTest"),
          new ThreadPoolExecutor.CallerRunsPolicy());

```





### 线程数设置通用计算公式:



CPU密集型任务: N + 1

这种任务主要消耗的是CPU资源，可以将线程数设置N(CPU核心数)+1，比CPU核心数多出一个线程是为了防止线程偶发的缺页中断，或者其他原因导致的任务暂停而带来的影响。一旦CPU处于究竟状态，而这种情况下，多出来的一个线程就可以充分利用CPU的空闲时间。



I/O密集型任务： 2N

这种任务启动后，大部分时间来处理I/O交互。而线程处理I/O不会占用CPU来处理，这时可以可将CPU交给其他线程使用，因此在I/O密集型任务中，可以多配制一些线程，一般来说可以配制为2N



那如何判断是I/O密集型还是CPU密集型呢？

CPU密集型就是利用CPU密集型的计算任务，比如在内存中进行大量的排序。

I/O密集型就是涉及网络读取、文件这类任务。这类任务的特点是CPU计算所耗费的时间比于IO操作完成的时间少很多。大部时间都花在等待IO操作完成上。



## 总结

这就是我的一些在线程池上的啃坑实录及其行为分析了，通过这些分析，我对jdk的线程池的行为为什么是这样了有了更深的理解，做到了知其然，更知其所以然，更是通过一个激进版的线程池，展示了优先开启线程再加入到队列的一个方案。对于线程池，总结了几个最佳实践：

1. 线程池一定要手动创建，Executors提供的快捷创建方法虽然简单，但隐藏了参数细节，如果不熟悉这些快捷创建线程池的方式，胡乱使用，导致的将一个个的事故。在使用线程池时，一定要要有根据使用场景，合理的配制核心线程数、最大线程数、任务队列、拒绝策略、及线程回收策略，以及一个明确的线程命名，就可以对问题进行排查。
2. 混用线程池也有很大的坑。对于CPU密集型任务以及IO密集型任务，对于资源的需求不同，选择不同的线程池。如果混用，必然导致相互干扰，其结果就是更低的性能，这个最好的实践还是将各种不同类型的任务分别创建线程池，以避免相互干扰。



对于这些代码，我已经上传[github](https://github.com/kkzfl22/demojava8/blob/master/src/main/java/com/liujun/thread/threadpool/TaskThreadDataPoolGood.java)，可去github上查看。

























