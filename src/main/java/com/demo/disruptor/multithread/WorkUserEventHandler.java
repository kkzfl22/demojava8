/*
 * Copyright (C), 2008-2021, Paraview All Rights Reserved.
 */
package com.demo.disruptor.multithread;

import com.lmax.disruptor.WorkHandler;
import com.utils.CountRunNum;

/**
 * 事件处理
 *
 * @author liujun
 * @since 2021/4/10
 */
public class WorkUserEventHandler implements WorkHandler<WorkUserEventInfo> {

  /** 执行统计 */
  private CountRunNum runNum;

  public WorkUserEventHandler(CountRunNum runNum) {
    this.runNum = runNum;
  }

  @Override
  public void onEvent(WorkUserEventInfo workUserEventInfo) throws Exception {
    runNum.runCount();
  }
}
