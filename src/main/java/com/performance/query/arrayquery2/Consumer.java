/*
 * Copyright (C), 2008-2021, Paraview All Rights Reserved.
 */
package com.performance.query.arrayquery2;

import com.performance.query.common.DataInfo;
import com.utils.CountRunNum;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author liujun
 * @since 2021/4/10
 */
public class Consumer implements Runnable {

  private final ArrayBlockingQueue<DataInfo> queue;

  /** 运行信息 */
  private CountRunNum runNum;

  public Consumer(ArrayBlockingQueue<DataInfo> queue, CountRunNum runNum) {
    this.queue = queue;
    this.runNum = runNum;
  }

  @Override
  public void run() {
    while (true) {

      // 从队列中获取数据
      DataInfo data = null;
      try {
        data = queue.take();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      if (data == null) {
        continue;
      }

      runNum.runCount();
    }
  }
}
