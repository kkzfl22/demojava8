package com.liujun.asynchronous.completablefuture;

import com.liujun.asynchronous.nonblocking.invoke.threadpool.ScheduleTaskThreadPool;
import com.sun.xml.internal.ws.util.CompletedFuture;
import com.utils.ThreadUtils;
import org.junit.Assert;
import org.junit.Test;

import javax.validation.constraints.AssertFalse;
import java.sql.SQLOutput;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * java中的异步编程，java类库中提供了CompletableFuture机制可以很好的应对大部分的场景
 *
 * @author liujun
 * @version 0.0.1
 */
public class TestCompletableFuture {

  @Test
  public void completeFutureExample() {
    CompletableFuture cf = CompletableFuture.completedFuture("message");
    Assert.assertTrue(cf.isDone());
    // cf.getNow方法在future完成的情况下会返回结果，否则返回null
    Assert.assertEquals("message", cf.getNow(null));
  }

  /**
   * 异步执行通过ForkJoinPool实现， 它使用守护线程去执行任务。注意这是CompletableFuture的特性，
   * 其它CompletionStage可以override这个默认的行为。
   */
  @Test
  public void runAsyncExample() {
    CompletableFuture cf =
        CompletableFuture.runAsync(
            () -> {
              // 检查是否为守护进程
              Assert.assertTrue(Thread.currentThread().isDaemon());
              ThreadUtils.sleep(5);
            });

    // 当前任务未完成
    Assert.assertFalse(cf.isDone());
    ThreadUtils.sleep(6);
    Assert.assertTrue(cf.isDone());
  }

  /** 在前一个阶段上应用函数 */
  @Test
  public void thenApplyExample() {

    // 注意thenApply方法名称的行为
    // then 意味着这个阶段的动作发生在当前阶段正常完成之后。将前一个阶段完成后，执行一个函数
    CompletableFuture cf =
        CompletableFuture.completedFuture("message")
            .thenApply(
                s -> {
                  // 当前进程非守护进程
                  Assert.assertFalse(Thread.currentThread().isDaemon());
                  return s.toUpperCase();
                });

    Assert.assertEquals("MESSAGE", cf.getNow(null));
  }

  /** 在前一个阶段上异步应用函数 ，异步使用forkJoinPool来执行 */
  @Test
  public void thenApplyAsyncExample() {
    CompletableFuture cf =
        CompletableFuture.completedFuture("message")
            .thenApplyAsync(
                s -> {
                  // 当前的forjoin为守护进程
                  Assert.assertTrue(Thread.currentThread().isDaemon());
                  ThreadUtils.sleep(2);
                  return s.toUpperCase();
                });

    Assert.assertNull(cf.getNow(null));
    Assert.assertEquals("MESSAGE", cf.join());
  }

  /** 使用定制的线程池，在前一阶段上异步应用函数 */
  @Test
  public void thenApplyAsyncWithExecutorExample() {
    CompletableFuture cf =
        CompletableFuture.completedFuture("message")
            .thenApplyAsync(
                s -> {
                  System.out.println("线程池的名称:" + Thread.currentThread().getName());
                  ThreadUtils.sleep(2);
                  return s.toUpperCase();
                },
                ScheduleTaskThreadPool.INSTANCE.getPool());

    Assert.assertNull(cf.getNow(null));
    Assert.assertEquals("MESSAGE", cf.join());
    Assert.assertNotNull(cf.getNow(null));
  }

  /**
   * 在一个指定的线程池中执行任务
   *
   * @throws ExecutionException
   * @throws InterruptedException
   */
  @Test
  public void thenAsyncExecutor() throws ExecutionException, InterruptedException {
    CompletableFuture cf =
        CompletableFuture.runAsync(
            () -> {
              System.out.println("任务执行。。。");
              ThreadUtils.sleep(2);
              System.out.println("任务结束。。。");
            },
            ScheduleTaskThreadPool.INSTANCE.getPool());

    // 等待任务执行完毕
    System.out.println(cf.get());
  }

  /**
   * 消费前一阶段的结果
   *
   * <p>如果下一个阶段接收了当前阶段的结果，但是在计算的时候不需要返回，那么它可以不应用一个函数，而是一个消费者，调用方法也变成了thenAccept
   */
  @Test
  public void thenAcceptExample() {
    StringBuilder result = new StringBuilder();
    CompletableFuture cf =
        CompletableFuture.completedFuture("thenAccept Message").thenAccept(s -> result.append(s));
    System.out.println(result);
    Assert.assertTrue("Result was empty", result.length() > 0);
  }

  /** 计算完成异常的情况 */
  @Test
  public void completeExceptionallyExample() {

    CompletableFuture cf =
        CompletableFuture.completedFuture("message")
            .thenApplyAsync(
                s -> {
                  // 当前的forjoin为守护进程
                  Assert.assertTrue(Thread.currentThread().isDaemon());
                  ThreadUtils.sleep(2);
                  return s.toUpperCase();
                });

    // 异步处理器
    CompletableFuture exceptionHandler =
        cf.handle(
            (s, th) -> {
              return (th != null) ? "message upon cancel" : "";
            });

    // 构建一个异步
    cf.completeExceptionally(new RuntimeException("completed Exceptionally"));

    try {
      cf.join();
      Assert.fail("should have thrown an Exception");
    } catch (Exception e) {
      e.printStackTrace();
      Assert.assertEquals("completed Exceptionally", e.getCause().getMessage());
    }

    Assert.assertEquals("message upon cancel", exceptionHandler.join());
  }

  @Test
  public void cancelExample() {
    CompletableFuture cf =
        CompletableFuture.completedFuture("message")
            .thenApplyAsync(
                s -> {
                  ThreadUtils.sleep(3);
                  return s.toUpperCase();
                });

    CompletableFuture cf2 = cf.exceptionally(throwable -> "canceled message");

    // 执行取消
    Assert.assertTrue("Was not canceled", cf.cancel(true));

    System.out.println(cf.isCompletedExceptionally());

    Assert.assertTrue("Was not completed exceptionally", cf.isCompletedExceptionally());
    Assert.assertEquals("canceled message", cf2.join());
  }

  @Test
  public void whenFinish() {
    ThreadUtils.sleep(10);
    System.out.println("开始运行!.....");

    CompletableFuture<String> cf =
        CompletableFuture.completedFuture("response1")
            .thenApply(
                s -> {
                  System.out.println("线程名称:" + Thread.currentThread().getName());
                  // 执行任务
                  ThreadUtils.sleep(5);
                  System.out.println("任务1执行完毕:");

                  return s.toUpperCase();
                });

    CompletableFuture<String> cf2 = cf.thenCompose(s -> runRsp(s));

    cf2.whenComplete(
        (result, throwable) -> {
          if (throwable != null) {
            throwable.printStackTrace();
            return;
          }
          System.out.println(result);
        });
  }

  private CompletableFuture runRsp(String data) {
    CompletableFuture future = new CompletableFuture();
    future.thenCompose(
        s -> {
          System.out.println("任务2开始执行");
          ThreadUtils.sleep(10);
          return s;
        });
    future.complete("run2");
    return future;
  }
}
