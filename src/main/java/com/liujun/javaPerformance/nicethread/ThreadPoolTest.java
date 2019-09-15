package com.liujun.javaPerformance.nicethread;

import java.util.Arrays;
import java.util.concurrent.*;

/**
 * 进行threadpool使用测试
 *
 * @author liujun
 * @version 0.0.1
 * @date 2019/08/13
 */
public class ThreadPoolTest {

  private static double[] d;

  private static class ThreadPoolExecutorTask implements Callable<Integer> {
    private int first;
    private int last;

    public ThreadPoolExecutorTask(int first, int last) {
      this.first = first;
      this.last = last;
    }

    @Override
    public Integer call() throws Exception {
      int subCount = 0;

      for (int i = first; i <= last; i++) { //
        if (d[i] < 0.5) {
          subCount++;
        }
      }
      return subCount;
    }
  }

  public static void main(String[] args) throws ExecutionException, InterruptedException {
    int maxSize = 10000000;
    d = alloct(maxSize);

    //    System.out.println(Arrays.toString(d));

    ThreadPoolExecutor tpe =
        new ThreadPoolExecutor(4, 4, Long.MAX_VALUE, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

    Future[] f = new Future[4];

    int size = d.length / 4;

    for (int i = 0; i < 3; i++) {
      f[i] = tpe.submit(new ThreadPoolExecutorTask(i * size, (i + 1) * size - 1));
    }
    f[3] = tpe.submit(new ThreadPoolExecutorTask(3 * size, d.length - 1));

    Integer n = 0;
    for (int i = 0; i < 4; i++) {
      n += (Integer) f[i].get();
    }
    System.out.println("found " + n);
  }

  public static double[] alloct(int maxSize) {
    double[] d = new double[maxSize];
    for (int i = 0; i < maxSize; i++) {
      d[i] = ThreadLocalRandom.current().nextDouble(0.00001, 1);
    }

    return d;
  }
}
