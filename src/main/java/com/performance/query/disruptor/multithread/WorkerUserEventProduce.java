/*
 * Copyright (C), 2008-2021, Paraview All Rights Reserved.
 */
package com.performance.query.disruptor.multithread;

import com.lmax.disruptor.RingBuffer;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * @author liujun
 * @since 2021/4/11
 */
public class WorkerUserEventProduce {

  private RingBuffer<WorkUserEventInfo> ringBuffer;

  public WorkerUserEventProduce(RingBuffer<WorkUserEventInfo> ringBuffer) {
    this.ringBuffer = ringBuffer;
  }

  public void onData(int currIndex) {
    long nextSequence = ringBuffer.next();
    try {
      WorkUserEventInfo workUser = ringBuffer.get(nextSequence);
      workUser.setIndex(currIndex);
      workUser.setData(RandomStringUtils.randomAlphabetic(20));
    } finally {
      ringBuffer.publish(nextSequence);
    }
  }
}
