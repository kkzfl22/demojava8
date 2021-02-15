package com.liujun.thread.threadpool;

import org.junit.Test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;

/**
 * 测试线程池
 *
 * @author liujun
 * @version 0.0.1
 */
public class TestTaskThreadDataPoolBad {

  @Test
  public void dataTest() {
    // 声明线程池
    TaskThreadDataPoolBad taskPool =
        new TaskThreadDataPoolBad(1, 4, 30, TimeUnit.SECONDS, 8, new TaskThreadFactory());

    for (int i = 0; i < 30; i++) {
      taskPool.submit(
          () -> {
            System.out.println("提交任务");

            try {
              Thread.sleep(100000);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
            System.out.println("任务结束! ");
          });
    }

    BlockingQueue<Runnable> queue =
        new LinkedTransferQueue<Runnable>() {
          @Override
          public boolean offer(Runnable e) {
            return tryTransfer(e);
          }
        };

  }
}
