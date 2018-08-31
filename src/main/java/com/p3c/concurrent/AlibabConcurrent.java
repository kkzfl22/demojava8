package com.p3c.concurrent;

/**
 * 并发处理
 *
 * <p>1,获取单例对象需要保证线程安全，其中的方法也要保证线程安全
 *
 * <p>2，在创建 线程或线程池时，请指定有意义的线程名称，方便出错时回溯
 *
 * <p>3,线程资源必须通过线程池提供，不能允许在应用中自行显示创建 线程。
 *
 * <p>4，线程池不允许使用Executors创建，而是通过ThreeadPoolExecutor的方式创建，这样的处理方式能让编写代码的工程师更加明确线程池的运行规则，规则资源耗尽的风险
 *
 * <p>Executors返回的线程池对象弊端
 *
 * <p>4.1）FixedThreadPool和SingleThreadPool:允许的请求队列长度为Integer.MAX_VALUE,可能会堆积大量的请求，从而造成OOM.
 *
 * <p>4.2)CacheThreadPool和ScheduleThreadPol:允许创建 的线程数量为Inter.Max_VALUE,可能会创建大量的线程，从而导致OOM
 *
 * <p>5,SimpDateFormat是线程不安全的类，一般要定为static变量，如果定义为static，必须加锁，或者使用DateUtils工具类,可使用DataFormatUtils推荐的方式
 * 在JDK8中可以使用Instant代替Date,LocalDateTime代替Calendar,DateTimeFormatter代替SimpleDateFormat，
 *
 * <p>6,在高并发场景中，同步调用应该去考量锁的性能损耗，能用无锁数据结构，就不要用锁；能锁区块，就不要锁整个方法；能用对象锁，就不要用类锁
 * 说明，加锁的代码块工作量尽可能小，避免在锁代码中高用RPC方法
 *
 * <p>7，在对多个资源、数据库表、对象同时加锁时、需要保持一致的加锁顺序，否则可能会造成死锁
 *
 * 8，在并发修改同一记录时，为避免去更新丢失，需要加锁，要么应用层加锁，要么在缓存层加锁，要么在数据库层使用乐观锁，使用version作为更新依据
 * 说明：每次访问冲突概率小于20%，推荐使用乐观锁，否则使用悲观锁，乐观锁的重试次数不得小于3次
 *
 * 9，对于多线程并行处理定时任务的情况，在Timer运行多个TimeTask时，只要其中之一没有捕获异常，其他任务便会自动终止运行。
 * 如果在处理定时任务时，使用SheduleExceutorService，则没有这个问题
 *
 * 10,使用CountDownLatch，进行异步转同步，每个线程退出前必须调用countDown方法，线程执行代码注意catch异常，确保countdown方法被执行。
 * 避免方线程无法执行至await方法，直到超时才返回结果.
 *
 * 11,避免Randomp实例被多个线程使用，虽然共享该实例是线程安全的，但会因为竞争同一seed导致性能下降.推荐使用ThreadLocalRandom
 *
 * 12,在并发场景下，使用双重检查锁（double-chedked-locking)实现延迟初始化的优化问题隐患，推荐解决方案中较为简单的一种，即目标属性声明为Volatile型
 *
 * 13,参考volatile解决多线程内存不可见问题，对于一写多读取，可以解决变量同步问题，但是如果多写，同样无法解决线程安全问题，如果是Count++操作
 * 使用AtomicInteger，addAndGet方法，如果是JDK8，推荐使用LongAdder对象，它比AtomicLong的性能更好（减少乐观锁的重试次数）
 *
 * 14,HashMap在容量不够进行resize时，由于高并发可能出现死链，怡莲蚕桑CPU占用飙升
 *
 * 15，ThreadLocal无法解决共享对象的更新问题，，ThreadLocal对象建议使用static修饰。这个变量是针对一个线程内所有操作共享的，
 * 所以设置为静态变量，所有此类实例共享此静态变量，在类第一次使用时装载，只分配一块存储空间，所有此类的对象，都可以操控这个变量
 */
public class AlibabConcurrent {}
