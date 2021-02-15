package com.liujun.thread.threadpool;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 测试线程池的放入操作
 *
 * @author liujun
 * @version 0.0.1
 */
public class TestTaskThreadDataPoolDefault {

  @Test
  public void testRunAble() {

    // 声明线程池
    TaskThreadDataPoolDefault taskPool = new TaskThreadDataPoolDefault();

    for (int i = 0; i < 30; i++) {
      final int index = i;
      taskPool.submit(
          () -> {
            System.out.println("提交任务" + index);

            try {
              Thread.sleep(10000);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
            System.out.println("任务结束! " + index);
          });
    }
  }

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
}
