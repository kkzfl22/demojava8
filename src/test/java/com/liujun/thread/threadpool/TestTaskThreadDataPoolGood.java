package com.liujun.thread.threadpool;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 测试线程池的放入操作
 *
 * @author liujun
 * @version 0.0.1
 */
public class TestTaskThreadDataPoolGood {

  @Test
  public void testRunAble() {

    // 声明线程池
    TaskThreadDataPoolGood taskPool = new TaskThreadDataPoolGood();

    LinkedList<Future> dataRun = new LinkedList<>();

    for (int i = 0; i < 30; i++) {
      final int index = i;

      Future dataRsp =
          taskPool.submit(
              () -> {
                try {
                  Thread.sleep(ThreadLocalRandom.current().nextInt(100, 3000));
                } catch (InterruptedException e) {
                  e.printStackTrace();
                }
              });

      // 在链表尾加入一个节点
      dataRun.addLast(dataRsp);

      if (dataRun.size() >= taskPool.maxData()) {
        Future Rsp = dataRun.pop();
        try {
          Rsp.get();
        } catch (InterruptedException e) {
          e.printStackTrace();
        } catch (ExecutionException e) {
          e.printStackTrace();
        }
      }
    }

    // 结束后都进行等待
    for (Future fuRsp : dataRun) {
      try {
        fuRsp.get();
      } catch (InterruptedException e) {
        e.printStackTrace();
      } catch (ExecutionException e) {
        e.printStackTrace();
      }
    }
  }

  @Test
  public void testCallAble() {
    // 声明线程池
    TaskThreadDataPoolGood taskPool = new TaskThreadDataPoolGood();
    LinkedList<Future> dataRun = new LinkedList<>();

    int DataIndex = 0;

    for (int i = 0; i < 30; i++) {
      final int index = i;
      Future dataRsp =
          taskPool.submit(
              () -> {
                System.out.println("运行.." + index);
                return index;
              });

      // 在链表尾加入一个节点
      dataRun.addLast(dataRsp);

      if (dataRun.size() >= taskPool.maxData()) {
        Future Rsp = dataRun.pop();
        try {
          int rsp = (Integer) Rsp.get();
          Assert.assertEquals(rsp, DataIndex);
          DataIndex++;
        } catch (InterruptedException e) {
          e.printStackTrace();
        } catch (ExecutionException e) {
          e.printStackTrace();
        }
      }
    }

    // 结束后都进行等待
    for (Future fuRsp : dataRun) {
      try {
        int rsp = (Integer) fuRsp.get();
        Assert.assertEquals(rsp, DataIndex);
        DataIndex++;
      } catch (InterruptedException e) {
        e.printStackTrace();
      } catch (ExecutionException e) {
        e.printStackTrace();
      }
    }
  }

  @Test(expected = RejectedExecutionException.class)
  public void fullException() {
    // 声明线程池
    TaskThreadDataPoolGood taskPool = new TaskThreadDataPoolGood();

    for (int i = 0; i < 30; i++) {
      final int index = i;
      taskPool.submit(
          () -> {
            try {
              System.out.println("任务开始----------->:" + index);

              Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 3000));
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
            System.out.println("任务结束! " + index);
          });
    }
  }

  @Test
  public void TestActiveThreadPool() {
    TaskThreadDataPoolGood threadPool = TaskThreadDataPoolGood.INSTANCE;

    // 启动线程池的信息输出
    ThreadPrint.printStatusTimeOut(threadPool.getThreadPool());

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

    threadPool.getThreadPool().shutdown();
  }
}
