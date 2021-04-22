/*
 * Copyright (C), 2008-2021, Paraview All Rights Reserved.
 */
package com.performance.query.disruptor.multithread;

import com.liujun.asynchronous.nonblocking.invoke.threadpool.ScheduleTaskThreadPool;
import com.liujun.command.threadpool.TaskThreadDataPool;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.WorkerPool;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.performance.query.disruptor.base.UserEventInfo;
import com.utils.CountRunNum;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 多线程的实现版本
 *
 * @author liujun
 * @since 2021/4/10
 */
public class DisruptorMultiThread2 {

  public static void main(String[] args) throws InterruptedException {
    // 获取线程池
    // readPoolExecutor threadPool = ScheduleTaskThreadPool.INSTANCE.getPool();

    // 初始化事件的产生
    EventFactory factory = new UserEventFactory();

    // 初始化ringBuffer的大小，需为2的幂次
    int ringSize = 4096;

    // 设置等待策略
    WaitStrategy waitStrategy = new BlockingWaitStrategy();

    // 创建ringbuffer对象
    Disruptor<WorkUserEventInfo> disruptor =
        new Disruptor<WorkUserEventInfo>(
            factory,
            ringSize,
            ScheduleTaskThreadPool.INSTANCE.threadFactory,
            ProducerType.MULTI,
            waitStrategy);

    // 创建统计信息
    CountRunNum newRunNum = CountRunNum.newInstance();
    int workSize = 4;
    WorkHandler[] eventHandlers = new WorkUserEventHandler[workSize];
    for (int i = 0; i < workSize; i++) {
      eventHandlers[i] = new WorkUserEventHandler(newRunNum);
    }
    disruptor.handleEventsWithWorkerPool(eventHandlers);

    // 启动线程池处理
    disruptor.start();

    RingBuffer<WorkUserEventInfo> ringBuffer = disruptor.getRingBuffer();

    WorkerUserEventProduce produce = new WorkerUserEventProduce(ringBuffer);

    System.out.println("...");

    for (int i = 0; i <= 10000020; i++) {
      produce.onData(i);
    }
    System.out.println("finish..");
  }
}
