package com.liujun.thread.threadpool.mix.web;

import com.liujun.thread.threadpool.TaskThreadFactory;
import com.liujun.thread.threadpool.ThreadPrint;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.File;
import java.nio.file.Files;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 混用线程池程序
 *
 * @author liujun
 * @version 0.0.1
 */
@RestController
@RequestMapping("/demo")
public class MixThreadPoolFacade {

  /** CPU密集型 */
  private ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(1000);

  /** 线程池信息 */
  private ThreadPoolExecutor threadPool =
      new ThreadPoolExecutor(
          32,
          64,
          30,
          TimeUnit.SECONDS,
          queue,
          new TaskThreadFactory("task-cpu-"),
          new ThreadPoolExecutor.CallerRunsPolicy());

  /** Io密集型 */
  private ArrayBlockingQueue<Runnable> ioQueue = new ArrayBlockingQueue<>(512);

  /** IO密集型线程池信息 */
  private ThreadPoolExecutor ioThreadPool =
      new ThreadPoolExecutor(
          2,
          2,
          30,
          TimeUnit.SECONDS,
          ioQueue,
          new TaskThreadFactory("task-io-"),
          new ThreadPoolExecutor.CallerRunsPolicy());

  /** 最大深度 */
  private static final int MAX_DEEP = 204800;

  @PostConstruct
  public void init() {
    ThreadPrint.printStatus(threadPool);

    new Thread(
            () -> {
              // 向线程池中提交IO密集型任务
              ioThreadPool.submit(
                  () -> {
                    while (true) {
                      fileReader(new File("d:"), 0);
                      try {
                        Thread.sleep(100);
                      } catch (InterruptedException e) {
                        e.printStackTrace();
                      }
                    }
                  });
            })
        .start();
  }

  /**
   * 使用文件读取模拟IO的压力
   *
   * @param file
   * @param max
   */
  private void fileReader(File file, int max) {
    if (max > MAX_DEEP) {
      return;
    }
    if (file.isFile()) {
      return;
    }
    // 文件夹遍历读取
    else if (file.isDirectory()) {
      for (File item : file.listFiles()) {
        this.fileReader(item, max + 1);
      }
    }
  }

  @RequestMapping(
      value = "/maxThread",
      method = {RequestMethod.GET, RequestMethod.POST})
  public int mixThread() throws ExecutionException, InterruptedException {

    return threadPool
        .submit(
            () -> {
              try {
                Thread.sleep(20);
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
              return 1;
            })
        .get();
  }
}
