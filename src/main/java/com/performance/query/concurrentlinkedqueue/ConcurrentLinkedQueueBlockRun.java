/*
 * Copyright (C), 2008-2021, Paraview All Rights Reserved.
 */
package com.performance.query.concurrentlinkedqueue;

import com.liujun.command.threadpool.TaskThreadDataPool;
import com.utils.CountRunNum;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 入门示例1
 *
 * @author liujun
 * @since 2021/4/10
 */
public class ConcurrentLinkedQueueBlockRun {

  public static void main(String[] args) throws InterruptedException {
    // 获取线程池
    TaskThreadDataPool exec = TaskThreadDataPool.INSTANCE;

    ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();

    CountRunNum runNum1 = CountRunNum.newInstance();

    // 1,事件的创建
    exec.submit(new Consumer(queue, runNum1));
    // exec.submit(new Consumer(queue, runNum1));
    // exec.submit(new Consumer(queue, runNum1));

    // 2,事件的处理
    int maxNum = 10000020;
    for (int i = 0; i <= maxNum; i++) {
      queue.add(RandomStringUtils.randomAlphabetic(10));
    }
  }
}
