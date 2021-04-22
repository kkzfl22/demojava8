/*
 * Copyright (C), 2008-2021, Paraview All Rights Reserved.
 */
package com.performance.query.disruptor.multithread;

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

  public static void main(String[] args) throws InterruptedException {
    // 获取线程池
    ThreadPoolExecutor threadPool = TaskThreadDataPool.INSTANCE.getThreadPool();

    // 初始化事件的产生
    EventFactory factory = new UserEventFactory();

    // 初始化ringBuffer的大小，需为2的幂次
    int ringSize = 128;

    // 设置等待策略
    WaitStrategy waitStrategy = new BlockingWaitStrategy();

    // 创建ringbuffer对象
    RingBuffer<WorkUserEventInfo> ringBuffer =
        RingBuffer.create(ProducerType.MULTI, factory, ringSize, waitStrategy);

    // 创建屏障
    SequenceBarrier barrier = ringBuffer.newBarrier();

    // 创建统计信息
    CountRunNum newRunNum = CountRunNum.newInstance();

    int work_size = 4;
    WorkUserEventHandler[] eventHandlers = new WorkUserEventHandler[work_size];
    for (int i = 0; i < work_size; i++) {
      eventHandlers[i] = new WorkUserEventHandler(newRunNum);
    }

    WorkerPool<WorkUserEventInfo> workPool =
        new WorkerPool(ringBuffer, barrier, new UserExceptionHandler(), eventHandlers);

    // 启动线程池处理
    workPool.start(threadPool);

    final int maxNum = 5120;
    final int increment = 64;

    WorkerUserEventProduce produce = new WorkerUserEventProduce(ringBuffer);

    int target = 64;
    for (int i = 0; i <= maxNum; i++) {
      produce.onData(i);
      if (i == target) {
        System.out.println("当前放入:" + i);
        target = increment + target;
      }
    }
    System.out.println("finish..");

    for (int i = 0; i < 10; i++) {
      newRunNum.print();
      Thread.sleep(500);
    }
  }
}
