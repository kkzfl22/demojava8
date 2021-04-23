# 总结JAVA中各种锁的使用

## 1. 为什么要使用锁?

​    拿个生活中的示例来说明这个问题吧，某超市做促销，菜仔油半价卖，35块钱一桶。这消息一出必然会有很多的人抢购。但菜仔油不是无限供应的，超市通常会把菜仔油放到一个地方，每次放进去一个或者两个人。这些人先购结束了，再放下一波人，直到油卖光了，就结束了。

  类比卖超市，想像下单程序是不是也是这样子呢，我们的客户通过网页或者手机下单商品。服务器在收到请求后都需要进行扣减库存操作。当非常多的客户同时下单了某一个商品。这样导致大量的请求都需要去扣库存。这时候我们通常就需要进行加锁操作。以将并行的请求变为串行，以防止商品越卖的情况。



## 2. 锁的演变

  单体应用我们可以使用java提供的并发处理相关的API进行控制。但是随着我们需要服务越来越多的客户。已经无法再使用单体应用去服务我们现在的互联网的用户了，现在架构都在从单体应用架构演变为分布式微服务架构。跨JVM或者 跨进程的实例部署，就没有通过java的锁机制来控制并发了。 为了解决这种跨JVM并发访问控制的问题，就需要一种跨JVM的互斥机制，来控制共享资源的访问。这时候就需要引入分布式锁。

那将这些演示的中出现的锁进行总结。可以归纳为这样几类：

![](D:\doc\博客\总续实践\java中的锁\锁的演变.png)



## 3. 锁的分类

锁一般分为两类：乐观锁与悲观锁。

乐观锁，这是一种积极向上的心态，类比现实就是这个世界还是好人多，所以乐观锁并不会去加锁，在更新数据时做下数据的验证。

悲观锁。这个就是相对乐观锁来说就是另外一种心态了，总是假设最坏的情况，每次操作数据别人都有可能会改变，所以在操作资源前需要获取锁，然后进行操作，操作完了，再释放锁，别人就可以获取到锁，操作资源了。

那什么场景适用乐观锁，什么场景又适用于悲观锁呢？

如果一个应用是读多写入，这类就适合乐观锁。

如果一个应用是写多读少，这类就适合悲观锁。

![](D:\doc\博客\总续实践\java中的锁\锁的分类.png)



## 4.不加锁的问题

这是一个商量下单的示例。Goods为商品服务，Orders为订单服务，每个订单去扣减一笔库存，这样一个简单的示例。

当出现并发下订单，程序还能正常将库存扣减成功吗？

商品信息Goods

```java
public class Goods {

  /** 商品名称 */
  private String name;

  /** 商品库存数量 */
  private int goodsNum;

  public Goods(String name, int goodsNum) {
    this.name = name;
    this.goodsNum = goodsNum;
  }

  /** 商品的库存扣减操作 */
  public void minusGoods(int num) {
    goodsNum -= num;
  }

  /**
   * 检查是否足够下单
   *
   * @return false，当前商品已经不足已下单 true 当前库在意可以下单
   */
  public boolean sellOut(int nums) {
    if (goodsNum >= nums) {
      return true;
    }
    return false;
  }

  /**
   * 获取商品数量
   *
   * @return 当前商品的数量
   */
  public int getGoods() {
    return goodsNum;
  }
}
```

订单服务Orders

```java
public class Orders {

  /** 商品服务 */
  private Goods goods;

  public Orders(Goods goods) {
    this.goods = goods;
  }

  /**
   * 创建订单
   *
   * @return
   */
  public boolean createOrder(int num) {

    // 执行商品的减库存操作
    if (goods.sellOut(num)) {
      // 执行扣减库存操作
      goods.minusGoods(num);
      return true;
    }
    return false;
  }

  public int getGoods() {
    return goods.getGoods();
  }
}
```

执行测试:

```java
public class TestOrders {

  @Test
  public void useOrder() throws InterruptedException {
    int orderNumSum = 800;
    Goods goods = new Goods("mac", orderNumSum);

    // 并发进行下单操作
    int maxOrder = 8;

    int count = 0;
    for (int i = 0; i < orderNumSum / maxOrder; i++) {
      CountDownLatch startLatch = new CountDownLatch(maxOrder);
      for (int j = 0; j < maxOrder; j++) {
        TaskThreadPool.INSTANCE.submit(
            () -> {
              startLatch.countDown();

              Orders instance = new Orders(goods);
              instance.createOrder(1);
            });

        count++;
      }
      // 确保任务都已经开始并行运行
      try {
        startLatch.await();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    System.out.println("结束,共运行:" + count + "次");

    TaskThreadPool.INSTANCE.shutdown();

    Thread.sleep(500);

    System.out.println("shutdown status:" + TaskThreadPool.INSTANCE.getPool().isShutdown());

    System.out.println("最后商品的库存:" + goods.getGoods());
  }
}
```



我们可以多次运行看到结果的输出:

```java
结束,共运行:800次
shutdown status:true
最后商品的数量:3
```

```
结束,共运行:800次
shutdown status:true
最后商品的库存:1
```

```
结束,共运行:800次
shutdown status:true
最后商品的库存:4
```

从结果中，可以看到，库存没有正确的扣减，如果对应到现实情况就是客户下单了，却没有将库现进行正确的扣减，会出现超卖的情况。

那为什么会出现这个情况呢？

再来看看商品的扣减库存的动作。

```
  /** 商品的库存扣减操作 */
  public void minusGoods(int num) {
    goodsNum -= num;
  }
```

这里采用的java的直接减法操作。减法这个动作如果在计算机上的动作指令我们可以简单的理解为3个步骤。

1. 从主内存读取当前商品的数量至线程内。
2. 在线程栈内进行减法运算。
3. 将线程栈的运行的结果写回至主内存。

这时候如果多个线程同时操作这个库存数量。会出现什么情况呢？

![](D:\doc\博客\总续实践\java中的锁\出现并发错误的场景.png)

由于三个线程都将本地线程中的值重新设置到主存中，这就会发生数据库存少扣的情况。具体来说就是thread1和thread2拿到了库存数量80，同时进行库存在扣减操作，thread1操作完成后，就将num的值79写回主存。这时thread2也扣减完成，结果也是79，这样就导致了一次少扣库存的情况。





## 4.synchronize

接下来以最常用的synchronize为例，来解决常用的并发问题。



```java
public class Goods {

  private String name;
  private int goodsNum;

  public Goods(String name, int goodsNum) {
    this.name = name;
    this.goodsNum = goodsNum;
  }

  public synchronized void minusGoods(int num) {
    goodsNum -= num;
  }

  /**
   * 检查是否足够下单
   *
   * @return false，当前商品已经不足已下单 true 当前库存可以下单
   */
  public synchronized boolean sellOut(int nums) {
    if (goodsNum >= nums) {
      return true;
    }
    return false;
  }

  /**
   * 获取商品数量
   *
   * @return 当前商品的数量
   */
  public int getGoods() {
    return goodsNum;
  }
}
```



其他代码不变(包括单元测试)，这时再来运行单元测试可以发现:

```
结束,共运行:800次
shutdown status:true
最后商品的库存:0
```

库存被正确的扣减了。

这是我们最常用的保证并发的方案了。





## 5.lock

有了synchronized，为什么还会出现lock关键字呢？这个问题还是锁粒度的情问。synchronized关键字一般是方法锁或者对象锁，锁的粒度大，这也导致等待耗时时间也更长。而lock对象主代码块级别的锁，对于锁的粒度更小，仅在需要加锁的代码段上加锁操作。接下来将以lock方案来看看解决并发问题。

```java
public class Goods {

  /** 商品名称 */
  private String name;

  /** 商品库存数量 */
  private int goodsNum;

  /** 锁 */
  private Lock lock = new ReentrantLock();

  public Goods(String name, int goodsNum) {
    this.name = name;
    this.goodsNum = goodsNum;
  }

  /** 商品的库存扣减操作 */
  public void minusGoods(int num) {
    lock.lock();
    try {
      // 数量检查
      if (this.sellOut(num)) {
        goodsNum = goodsNum - num;
      }
    } finally {
      lock.unlock();
    }
  }

  /**
   * 检查是否足够下单
   *
   * @return false，当前商品已经不足已下单 true 当前库存可以可以下单
   */
  public boolean sellOut(int nums) {
    if (goodsNum >= nums) {
      return true;
    }
    return false;
  }

  /**
   * 获取商品数量
   *
   * @return 当前商品的数量
   */
  public int getGoods() {
    return goodsNum;
  }
}
```



再看看结果:

```
结束,共运行:800次
shutdown status:true
最后商品的库存:0
```











## 6.volatile

> volatile作为java中的关键字之一，用于声明变量的值会随时被别的线程修改，使用volatile修饰的变量会强制将修改的值立即写入主存，主存中的值设置会使缓存中的值失效（非volatile变量不具备这样的特性，非volatile变量的值会被缓存，线程A更新了这个值，线程B读取到这个变量的值可能读到的并不是线程A修改后的值）。volatile禁止指令重排。
>
> volatile具有可见性、有序性，不具备原子性。
>
> 原子性：类比事务，通常原子性指多个操作不存在只执行一部分的情况，如果全部执行完成没有问题，如果只执行了部分，那就得摊销已经执行的部分。
>
> 可见性：当多个线程访问同一个变量A时，线程1修改了A的值，其他线程能够立即读取到A的值
>
> 有序性： 即程序按照书写的先后顺序执行。这主要是应用指令重排序的问题。在java的内存模型中，允许编译器和处理器对指令进行重新排序。但重排序的指令不会影响到单个线程的执行。却会影响并发执行的正确性。
>
> volatile  适用场景：
>
> 适用于对变量的写入不依赖当前的值，对变量的读取不依赖volatile变量
>
> 适用于读多写入的场景。
>
> 可用作状态标识。

顺带来看下java的内存模型。

![](D:\doc\博客\总续实践\java中的锁\thread_stack.jpg)

存放在堆上的对象可以被所有持有对这个对象引用的线程访问。当一个线程可以访问一个对象时，它也可以访问这个对象的成员变量。如果两个线程同时调用同一个对象上的同一个方法，它们将会都访问这个对象的成员变量，但是每一个线程都拥有这个成员变量的私有拷贝。因为是私有拷贝，所以在写入数据在写入时发生覆盖。

还有一个很重要的概念:happens-before

>happens-before原则规则：
>
>1. 程序次序规则：一个线程内，按照代码顺序，书写在前面的操作先行发生于书写在后面的操作；
>2. 锁定规则：一个unLock操作先行发生于后面对同一个锁额lock操作；
>3. volatile变量规则：对一个变量的写操作先行发生于后面对这个变量的读操作；
>4. 传递规则：如果操作A先行发生于操作B，而操作B又先行发生于操作C，则可以得出操作A先行发生于操作C；
>5. 线程启动规则：Thread对象的start()方法先行发生于此线程的每个一个动作；
>6. 线程中断规则：对线程interrupt()方法的调用先行发生于被中断线程的代码检测到中断事件的发生；
>7. 线程终结规则：线程中所有的操作都先行发生于线程的终止检测，我们可以通过Thread.join()方法结束、Thread.isAlive()的返回值手段检测到线程已经终止执行；
>8. 对象终结规则：一个对象的初始化完成先行发生于他的finalize()方法的开始；

这里就讲到了volatile变量:这是一条比较重要的规则，它标志着volatile保证了线程可见性。通俗点讲就是如果一个线程先去写一个volatile变量，然后一个线程去读这个变量，那么这个写操作一定是happens-before读操作的。



由于单独volatile不能保证原子性，所以并不能实现示例要求的功能。



## 7.cas

还是以下单场景为例，使用CAS机制来实现一个扣减库存的示例：

```java
public class Goods {

  /** 商品名称 */
  private String name;

  /** 商品库存数量 添加volatile，保证内存的可见性 */
  private volatile int goodsNum;

  private static final Unsafe UNSAFE = getUnsafe();

  /** 值的偏移量 */
  private static final long valueOffset;

  /** 获取对象操作值的偏移量 */
  static {
    try {
      valueOffset = UNSAFE.objectFieldOffset(Goods.class.getDeclaredField("goodsNum"));
    } catch (Exception ex) {
      throw new Error(ex);
    }
  }

  public Goods(String name, int goodsNum) {
    this.name = name;
    this.goodsNum = goodsNum;
  }

  /**
   * 获取unsafe对象
   *
   * @return
   */
  public static Unsafe getUnsafe() {
    // 通过反射得到theUnsafe对应的Field对象
    Field field;
    try {
      field = Unsafe.class.getDeclaredField("theUnsafe");
      // 设置该Field为可访问
      field.setAccessible(true);
      // 通过Field得到该Field对应的具体对象，传入null是因为该Field为static的
      Unsafe unsafe = (Unsafe) field.get(null);
      return unsafe;
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return null;
  }

  /** 商品的库存扣减操作 */
  public void minusGoods(int num) {
    boolean updRsp;
    do {
      // 读取最新的volatile变量的值
      int goodsNumOld = UNSAFE.getIntVolatile(this, valueOffset);
      // 库存不足时，停止
      if (goodsNumOld - num < 0) {
        break;
      }
      int goodsNumNew = goodsNumOld - num;
      // 使用比较交换的原子操作执行更新
      updRsp = UNSAFE.compareAndSwapInt(this, valueOffset, goodsNumOld, goodsNumNew);
    } while (!updRsp);
  }

  /**
   * 检查是否足够下单
   *
   * @return false，当前商品已经不足已下单 true 当前库存可以可以下单
   */
  public boolean sellOut(int nums) {
    if (goodsNum >= nums) {
      return true;
    }
    return false;
  }

  /**
   * 获取商品数量
   *
   * @return 当前商品的数量
   */
  public int getGoods() {
    return goodsNum;
  }
}
```



运行结果:

```
结束,共运行:800次
shutdown status:true
最后商品的库存:0
```



这里使用一个Unsafe这个类，Unsafe类是在sun.misc包下，不属于Java标准。但是很多Java的基础类库，包括一些被广泛使用的高性能开发库都是基于Unsafe类开发的，比如Netty、Cassandra、Hadoop、Kafka等。Unsafe类在提升Java运行效率，增强Java语言底层操作能力方面起了很大的作用。

Unsafe类使Java拥有了像C语言的指针一样操作内存空间的能力，同时也带来了指针的问题。过度的使用Unsafe类会使得出错的几率变大，因此Java官方并不建议使用的。

此处使用两个操作getIntVolatile,从名字我们就可以看到，这是一个获取int类型的volatile修饰的变量的值。还有一个操作是compareAndSwapInt使用比较交换的原子操作，对数据进行修改操作。

如果你看过AtomicInteger的原码，你就会看到如上的代码的调用。

```java
    /**
     * Atomically sets the value to the given updated value
     * if the current value {@code ==} the expected value.
     *
     * @param expect the expected value
     * @param update the new value
     * @return {@code true} if successful. False return indicates that
     * the actual value was not equal to the expected value.
     */
    public final boolean compareAndSet(int expect, int update) {
        return unsafe.compareAndSwapInt(this, valueOffset, expect, update);
    }
```

如果在平时的开发中，还是建议直接使用AtomicInteger已经封装好的类。





## 总结

在这章节，我总结了在java中常用的一些锁的实现方案。分别是synchronize、lock、volatile、cas这几种实现方案，并分别给出了源码。在jdk的对于锁的优化从未停止，从重量级锁到无锁，在不同的场景下，选择合适的锁。而且源码中有非常好的一些思路值得学习。

下篇文章将继续分享数据库锁

参考资料:

https://zhuanlan.zhihu.com/p/29881777

https://www.cnblogs.com/chenssy/p/6393321.html

https://baijiahao.baidu.com/s?id=1595669808533077617&wfr=spider&for=pc

https://tech.meituan.com/2018/11/15/java-lock.html







