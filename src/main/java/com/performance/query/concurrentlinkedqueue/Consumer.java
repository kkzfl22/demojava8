/*
 * Copyright (C), 2008-2021, Paraview All Rights Reserved.
 */
package com.performance.query.concurrentlinkedqueue;

import com.utils.CountRunNum;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author liujun
 * @since 2021/4/10
 */
public class Consumer implements Runnable {

  private final ConcurrentLinkedQueue<String> queue;

  /** 运行信息 */
  private CountRunNum runNum;

  public Consumer(ConcurrentLinkedQueue<String> queue, CountRunNum runNum) {
    this.queue = queue;
    this.runNum = runNum;
  }

  @Override
  public void run() {
    while (true) {

      // 从队列中获取数据
      String data = queue.poll();

      if (data == null) {
        continue;
      }

      runNum.runCount();
    }
  }
}
