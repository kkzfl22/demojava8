/*
 * Copyright (C), 2008-2021, Paraview All Rights Reserved.
 */
package com.performance.query.disruptor.multithread;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;
import com.utils.CountRunNum;
import lombok.extern.slf4j.Slf4j;

/**
 * 事件处理
 *
 * @author liujun
 * @since 2021/4/10
 */
@Slf4j
public class WorkUserEventHandler
    implements WorkHandler<WorkUserEventInfo>, EventHandler<WorkUserEventInfo> {

  /** 执行统计 */
  private CountRunNum runNum;

  public WorkUserEventHandler(CountRunNum runNum) {
    this.runNum = runNum;
  }

  @Override
  public void onEvent(WorkUserEventInfo workUserEventInfo) throws Exception {
    // System.out.println("id:" + Thread.currentThread().getId() + "," + workUserEventInfo);

    // log.info("id:{} , info {} ", Thread.currentThread().getId(), workUserEventInfo);

    runNum.runCount();
  }

  @Override
  public void onEvent(WorkUserEventInfo workUserEventInfo, long l, boolean b) throws Exception {
    // System.out.println(
    //    "id:" + Thread.currentThread().getId() + "," + workUserEventInfo + ",sequence:" + l);
    runNum.runCount();
  }
}
