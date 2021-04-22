/*
 * Copyright (C), 2008-2021, Paraview All Rights Reserved.
 */
package com.demo.disruptor.multithread;

import com.liujun.command.threadpool.TaskThreadDataPool;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.WorkerPool;
import com.lmax.disruptor.dsl.ProducerType;
import com.utils.CountRunNum;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 多线程的实现版本
 *
 * @author liujun
 * @since 2021/4/10
 */
public class DisruptorMultiThreadDemo {

  public static void main(String[] args) {
    // 获取线程池
    ThreadPoolExecutor exec = TaskThreadDataPool.INSTANCE.getThreadPool();

    // 初始化事件的产生
    EventFactory factory = new UserEventFactory();

    // 初始化ringBuffer的大小，需为2的幂次
    int ringSize = 1024;

    // 设置等待策略
    WaitStrategy waitStrategy = new BlockingWaitStrategy();

    // 创建ringbuffer对象
    RingBuffer<WorkUserEventInfo> ringBuffer =
        RingBuffer.create(ProducerType.MULTI, factory, ringSize, waitStrategy);

    // 创建屏障
    SequenceBarrier barrier = ringBuffer.newBarrier();

    // 创建统计信息
    CountRunNum runNum = CountRunNum.newInstance();

    int work_size = 4;
    WorkUserEventHandler[] eventHandlers = new WorkUserEventHandler[work_size];

    for (int i = 0; i < work_size; i++) {
      eventHandlers[i] = new WorkUserEventHandler(runNum);
    }

    WorkerPool<WorkUserEventInfo> workPool =
        new WorkerPool(ringBuffer, barrier, new UserExceptionHandler(), eventHandlers);

    // 启动线程池处理
    workPool.start(exec);

    // 处理的模板代码
    long nextSequence = ringBuffer.next();
    try {
      WorkUserEventInfo user = ringBuffer.get(nextSequence);
      // 执行数据的填充操作
      user.setData(RandomStringUtils.randomAlphabetic(10));
      System.out.println("hello word !");
    } finally {
      // 通过序列号将数据发送出去
      ringBuffer.publish(nextSequence);
    }
  }
}
