/*
 * Copyright (C), 2008-2021, Paraview All Rights Reserved.
 */
package com.performance.query.disruptor.multithread;

import com.lmax.disruptor.EventFactory;

/**
 * @author liujun
 * @since 2021/4/10
 */
public class UserEventFactory implements EventFactory<WorkUserEventInfo> {

  @Override
  public WorkUserEventInfo newInstance() {
    WorkUserEventInfo userInfo = new WorkUserEventInfo();
    return userInfo;
  }
}
