package com.liujun.thread.threadpool;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池的默认策略中当核心任务满了后，将任务放入队列，当队列满了，启动最大线程，当达到最大线程池后，任务也满了，执行拒绝策略。
 *
 * @author liujun
 * @version 0.0.1
 */
public class TaskThreadDataPoolDefault {

  /** 实例信息 */
  public static final TaskThreadDataPoolDefault INSTANCE = new TaskThreadDataPoolDefault();

  /** 最大队列长度 */
  private static final int MAX_SIZE = 8;

  /** 队列信息 */
  private ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(MAX_SIZE);

  /** 最小核心线程数 */
  private static final int CORE_SIZE = 1;

  /** 最大的线程数 */
  private static final int MAX_POOL_SIZE = 4;

  /** 最大的保持的时间 */
  private static final int KEEP_ALIVE_TIME = 30;

  /** 线程池信息 */
  private ThreadPoolExecutor threadPool =
      new ThreadPoolExecutor(
          CORE_SIZE,
          MAX_POOL_SIZE,
          KEEP_ALIVE_TIME,
          TimeUnit.SECONDS,
          queue,
          new TaskThreadFactory("dataTest"),
          new ThreadPoolExecutor.CallerRunsPolicy());

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
    return threadPool;
  }

  /**
   * 提交任务至线程池
   *
   * @param dataRun 任务
   */
  public Future submit(Runnable dataRun) {
    return threadPool.submit(dataRun);
  }
}
