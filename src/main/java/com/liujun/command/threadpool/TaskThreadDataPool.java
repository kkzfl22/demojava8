package com.liujun.command.threadpool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池的默认策略中当核心任务满了后，将任务放入队列，当队列满了，启动最大线程，当达到最大线程池后，任务也满了，执行拒绝策略。
 *
 * <p>由于command的线程任务属性IO等待型，CPU并不会占用太多，所以，可适当增大核心线程数
 *
 * @author liujun
 * @version 0.0.1
 */
public class TaskThreadDataPool {

  /** 实例信息 */
  public static final TaskThreadDataPool INSTANCE = new TaskThreadDataPool();

  /** 最大队列长度 */
  private static final int MAX_SIZE = 8;

  /** 队列信息 */
  private ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(MAX_SIZE);

  /** 最小核心线程数 */
  private static final int CORE_SIZE = 2;

  /** 最大的线程数 */
  private static final int MAX_POOL_SIZE = 8;

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
          new TaskThreadFactory("command"),
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

  public void shutdown() {
    threadPool.shutdown();
  }
}
