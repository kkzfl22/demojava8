/*
 * Copyright (C), 2008-2021, Paraview All Rights Reserved.
 */
package com.performance.query.disruptor.multithread;

import com.lmax.disruptor.ExceptionHandler;

/**
 * @author liujun
 * @since 2021/4/11
 */
public class UserExceptionHandler implements ExceptionHandler<WorkUserEventInfo> {

  @Override
  public void handleEventException(
      Throwable throwable, long l, WorkUserEventInfo workUserEventInfo) {
    System.out.println("当前发生异常的信息:" + workUserEventInfo);
    throwable.printStackTrace();
    System.out.println(throwable);
  }

  @Override
  public void handleOnStartException(Throwable throwable) {
    System.out.println(throwable);
    throwable.printStackTrace();
  }

  @Override
  public void handleOnShutdownException(Throwable throwable) {

    System.out.println(throwable);
    throwable.printStackTrace();
  }
}
