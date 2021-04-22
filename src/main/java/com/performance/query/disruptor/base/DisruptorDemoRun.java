/*
 * Copyright (C), 2008-2021, Paraview All Rights Reserved.
 */
package com.performance.query.disruptor.base;

import com.liujun.command.threadpool.TaskThreadDataPool;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 入门示例1
 *
 * @author liujun
 * @since 2021/4/10
 */
public class DisruptorDemoRun {

  public static void main(String[] args) {
    // 获取线程池
    ThreadPoolExecutor exec = TaskThreadDataPool.INSTANCE.getThreadPool();

    // 初始化事件的产生
    EventFactory factory = new UserEventFactory();

    // 初始化ringBuffer的大小，需为2的幂次
    int ringSize = 1024;

    // 创建ringbuffer对象
    Disruptor<UserEventInfo> disruptor = new Disruptor<UserEventInfo>(factory, ringSize, exec);

    // 指定事件处理器
    disruptor.handleEventsWith(new UserEventHandler());


    // 开启disruptor,所有的操作必须的start的之前完成
    disruptor.start();

    // 获取ringBuffer对象
    RingBuffer<UserEventInfo> buffer = disruptor.getRingBuffer();

    int maxNum = 10000000;
    for (int i = 0; i <= maxNum; i++) {
      // 处理的模板代码
      long nextSequence = buffer.next();
      try {
        UserEventInfo user = buffer.get(nextSequence);
        // 执行数据的填充操作
        user.setData(RandomStringUtils.randomAlphabetic(10));
      } finally {
        // 通过序列号将数据发送出去
        buffer.publish(nextSequence);
      }
    }
  }
}
