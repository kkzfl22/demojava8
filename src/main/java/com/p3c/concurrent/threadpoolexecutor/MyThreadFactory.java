package com.p3c.concurrent.threadpoolexecutor;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadFactory;

/** 自定义的线程工厂实现 */
public class MyThreadFactory implements ThreadFactory {

  private static final String PREFIX = "WORK-";

  @Override
  public Thread newThread(@NotNull Runnable r) {
    // 仅设置一个线程名称
    Thread currThread = new Thread(r, PREFIX + Thread.currentThread().getId());

    return currThread;
  }
}
