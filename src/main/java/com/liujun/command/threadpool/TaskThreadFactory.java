package com.liujun.command.threadpool;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 给线程池中的线程设置一个有意义的名称，用于在dump线程栈后，可以很方便的对问题进行排查
 *
 * @author liujun
 * @version 0.0.1
 * @date 2020/02/16
 */
public class TaskThreadFactory implements ThreadFactory {

  /** 编号 */
  private final AtomicInteger threadNum = new AtomicInteger(0);

  /** 名称 */
  private final String name;

  public TaskThreadFactory(String name) {
    this.name = name;
  }

  @Override
  public Thread newThread(Runnable r) {
    Thread t = new Thread(r);
    t.setName(name + "-" + threadNum.incrementAndGet());
    return t;
  }
}
