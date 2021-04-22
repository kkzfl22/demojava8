/*
 * Copyright (C), 2008-2021, Paraview All Rights Reserved.
 */
package com.demo.disruptor.base1;

import com.lmax.disruptor.EventHandler;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 事件处理
 *
 * @author liujun
 * @since 2021/4/10
 */
public class UserEventHandler implements EventHandler<UserEventInfo> {

  private static AtomicLong dataLong = new AtomicLong();

  @Override
  public void onEvent(UserEventInfo userEventInfo, long sequence, boolean b) throws Exception {
    System.out.println(userEventInfo);
  }
}
