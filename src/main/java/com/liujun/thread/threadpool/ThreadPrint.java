package com.liujun.thread.threadpool;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池的一些相关输出信息
 *
 * @author liujun
 * @version 0.0.1
 */
public class ThreadPrint {

  /**
   * 执行打印线程池的核心参数
   *
   * @param threadPool 线程池信息
   */
  public static void printStatus(ThreadPoolExecutor threadPool) {
    String outSpit = "\t";
    StringBuilder outData = new StringBuilder();
    // 核心线程数
    outData.append(threadPool.getPoolSize()).append(outSpit);
    // 激活线程数
    outData.append(threadPool.getActiveCount()).append(outSpit);
    // 当前的队列的大小
    outData.append(threadPool.getQueue().size()).append(outSpit);
    // 完全的任务总数
    outData.append(threadPool.getCompletedTaskCount()).append(outSpit);

    System.out.println(outData.toString());
  }

  /**
   * 定时输出执行打印线程池的核心参数
   *
   * @param threadPool 线程池信息
   */
  public static void printStatusTimeOut(ThreadPoolExecutor threadPool) {
    // 定时任务操作
    Executors.newSingleThreadScheduledExecutor()
        .scheduleAtFixedRate(
            () -> {
              printStatus(threadPool);
            },
            0,
            1,
            TimeUnit.SECONDS);
  }
}
