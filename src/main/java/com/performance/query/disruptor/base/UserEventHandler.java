/*
 * Copyright (C), 2008-2021, Paraview All Rights Reserved.
 */
package com.performance.query.disruptor.base;

import com.lmax.disruptor.EventHandler;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 事件处理
 *
 * @author liujun
 * @since 2021/4/10
 */
public class UserEventHandler implements EventHandler<UserEventInfo> {

  private AtomicLong dataLong = new AtomicLong();

  private volatile long targetNum = 0;

  private volatile long increment = 1000000;

  private long scopeStart = System.currentTimeMillis();

  private long start = scopeStart;

  @Override
  public void onEvent(UserEventInfo userEventInfo, long sequence, boolean b) throws Exception {

    if (dataLong.get() == targetNum) {

      StringBuilder outData = new StringBuilder();

      long end = System.currentTimeMillis();

      long scopeMulti = end - scopeStart;
      outData.append("当前共收到").append(dataLong.get()).append("次,");
      outData.append("总用时:").append(end - start).append("毫秒,");
      outData.append("区间收到").append(increment).append("次,");
      outData.append("区间用时:").append(scopeMulti).append("毫秒,");

      System.out.println(outData.toString());

      scopeStart = System.currentTimeMillis();

      targetNum += increment;
    }

    dataLong.incrementAndGet();
  }
}
