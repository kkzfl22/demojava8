package com.liujun.thread.threadpool;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池的默认策略中当核心任务满了后，将任务放入队列，当队列满了，启动最大线程，当达到最大线程池后，任务也满了，执行拒绝策略。
 *
 * <p>自定义线程池，采用激进的策略，首先任务还是启动核心线程，当线程满了之后，将启动最大线程，当最大线程也在运行了，其他任务则加入到队列中，当队列也满了执行拒绝策略
 *
 * @author liujun
 * @version 0.0.1
 */
public class TaskThreadDataPoolGood2 {

  /** 实例信息 */
  public static final TaskThreadDataPoolGood2 INSTANCE = new TaskThreadDataPoolGood2();

  public TaskThreadDataPoolGood2() {
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
  private static final int CORE_SIZE = 1;

  /** 最大的线程数 */
  private static final int MAX_POOL_SIZE = 4;

  /** 最大的保持的时间 */
  private static final int KEEP_ALIVE_TIME = 60;



  /** 线程池信息 */
  private ThreadPoolExecutor THREAD_POOL =
      new ThreadPoolExecutor(CORE_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, QUEUE);

  public void init() {
    // 设置拒绝策略
    THREAD_POOL.setRejectedExecutionHandler(
        new RejectedExecutionHandler() {
          @Override
          public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            // System.out.println("队列大小:" + executor.getQueue().size());
            if (executor.getQueue().size() >= MAX_SIZE) {
              throw new RejectedExecutionException(
                  "Task " + r.toString() + " rejected from " + executor.toString());
            } else {
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
