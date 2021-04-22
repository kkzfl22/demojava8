/*
 * Copyright (C), 2008-2021, Paraview All Rights Reserved.
 */
package com.performance.query.arrayquery2;

import com.liujun.command.threadpool.TaskThreadDataPool;
import com.performance.query.common.DataInfo;
import com.utils.CountRunNum;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * 入门示例1
 *
 * @author liujun
 * @since 2021/4/10
 */
public class ArrayBlockRun {

  public static void main(String[] args) throws InterruptedException {
    // 获取线程池
    TaskThreadDataPool exec = TaskThreadDataPool.INSTANCE;

    int size = 4;

    ArrayBlockingQueue<DataInfo>[] queue1 = new ArrayBlockingQueue[4];

    for (int i = 0; i < size; i++) {
      queue1[i] = new ArrayBlockingQueue(1024);
    }

    CountRunNum runNum1 = CountRunNum.newInstance();

    // 1,事件的创建
    for (int i = 0; i < size; i++) {
      exec.submit(new Consumer(queue1[i], runNum1));
    }

    // 2,事件的处理
    int maxNum = 10000020;
    int index = 0;
    for (int i = 0; i <= maxNum; i++) {
      DataInfo dataInfo = new DataInfo();
      dataInfo.setId(i);
      dataInfo.setData(RandomStringUtils.randomAlphabetic(20));
      queue1[index].add(dataInfo);
      index++;
      if (index == size) {
        index = 0;
      }
    }
  }
}
