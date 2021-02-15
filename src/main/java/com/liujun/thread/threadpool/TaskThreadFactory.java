package com.liujun.thread.threadpool;

import java.util.concurrent.ThreadFactory;

/**
 * 给线程池中的线程设置一个有意义的名称，用于在dump线程栈后，可以很方便的对问题进行排查
 *
 * @author liujun
 * @version 0.0.1
 * @date 2019/08/31
 */
public class TaskThreadFactory implements ThreadFactory {

  private static final String DEF_PREFIX = "task-thread-";

  private final String prefixName;

  public TaskThreadFactory() {
    this.prefixName = DEF_PREFIX;
  }

  public TaskThreadFactory(String prefixName) {
    this.prefixName = prefixName;
  }

  @Override
  public Thread newThread(Runnable r) {
    // 仅设置一个线程名称
    Thread currThread = new Thread(r, prefixName);
    return currThread;
  }
}
