package com.liujun.thread.threadpool.problem.newfixpool;

import com.liujun.thread.threadpool.TaskThreadFactory;
import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 测试线程池
 *
 * @author liujun
 * @version 0.0.1
 */
public class TestThreadPool {

  /** 填充数据个数 */
  private static final int MAX_DATA_FULL = 512;

  @Test
  public void testNewFixedThreadPoolOom() {
    ExecutorService dataPool = Executors.newFixedThreadPool(4);
    for (int i = 0; i < Integer.MAX_VALUE; i++) {
      dataPool.submit(new DataSender(fullData()));
    }
  }


  @Test
  public void testNewFixedThreadPoolOom2() {
    //SingleThreadExecutor
    ExecutorService dataPool = Executors.newSingleThreadExecutor();
    for (int i = 0; i < Integer.MAX_VALUE; i++) {
      dataPool.submit(new DataSender(fullData()));
    }
  }

  @Test
  public void testThreadPoolOK() {
    ThreadPoolExecutor dataPool =
        new ThreadPoolExecutor(
            4,
            4,
            0,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(8),
            new TaskThreadFactory("demo"),
            new ThreadPoolExecutor.AbortPolicy());
    for (int i = 0; i < Integer.MAX_VALUE; i++) {
      dataPool.submit(new DataSender(fullData()));
    }
  }

  @Test
  public void testNewCacheThreadPoolOom() {
    ExecutorService dataPool = Executors.newCachedThreadPool();
    for (int i = 0; i < Integer.MAX_VALUE; i++) {
      dataPool.submit(new DataSender(fullData()));
    }
  }

  @Test
  public void testNewCacheThreadPoolOom2() {
    ExecutorService dataPool = Executors.newScheduledThreadPool(10);
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
        // System.out.println("任务执行");
        Thread.sleep(120000L);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      System.out.println("data final:" + data);
    }
  }
}
