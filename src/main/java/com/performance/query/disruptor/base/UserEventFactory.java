/*
 * Copyright (C), 2008-2021, Paraview All Rights Reserved.
 */
package com.performance.query.disruptor.base;

import com.lmax.disruptor.EventFactory;

/**
 * @author liujun
 * @since 2021/4/10
 */
public class UserEventFactory implements EventFactory<UserEventInfo> {

  @Override
  public UserEventInfo newInstance() {
    UserEventInfo userInfo = new UserEventInfo();
    return userInfo;
  }
}
