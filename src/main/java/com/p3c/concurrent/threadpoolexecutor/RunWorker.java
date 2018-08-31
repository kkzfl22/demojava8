package com.p3c.concurrent.threadpoolexecutor;

import java.util.concurrent.ThreadLocalRandom;

/** 当前为工作线程，执行线程具体的任务 */
public class RunWorker implements Runnable {

  /** 运行索引任务号 */
  private int runIndex;

  public RunWorker(int runIndex) {
    this.runIndex = runIndex;
  }

  /** 休眠第一时间 */
  private static final long SLEEP_ONE = 1000l;

  @Override
  public void run() {

    try {
      if (ThreadLocalRandom.current().nextInt() % 2 == 1) {
        System.out.println(
            "curr thread:"
                + Thread.currentThread().getName()
                + ",sleep :"
                + SLEEP_ONE
                + ",index:"
                + runIndex);
        Thread.sleep(SLEEP_ONE);
      } else {
        System.out.println(
            "curr thread:"
                + Thread.currentThread().getName()
                + ",sleep :"
                + SLEEP_ONE * 2
                + ",index:"
                + runIndex);
        Thread.sleep(SLEEP_ONE * 2);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
