package com.liujun.javaPerformance.nicethread;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 进行fork-join相关的测试操作
 *
 * @author liujun
 * @version 0.0.1
 * @date 2019/08/13
 */
public class ForkJoinCountJunitTest {

  @Test
  public void testRunCount() {

    for (int i = 0; i < 1000; i++) {
      run();
    }
  }

  private void run() {
    try {
      int maxValue = 10;

      double[] value = alloct(maxValue);
      // System.out.println(Arrays.toString(value));

      ForkJoinCountTest instance = new ForkJoinCountTest(value);

      ForkJoinPool fjcount = new ForkJoinPool(4);
      ForkJoinCountTest.ForkJoinTaskCount task = instance.getInstance(0, maxValue - 1);
      ForkJoinTask result = fjcount.submit(task);
      // System.out.println(result.get());

      CountRecursion curseion = new CountRecursion(value);
      int maxNum = curseion.rescursion(0, maxValue);
      // System.out.println(maxNum);

      Assert.assertEquals(result.get(), maxNum);
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (ExecutionException e) {
      e.printStackTrace();
    }
  }

  public static double[] alloct(int maxSize) {
    double[] d = new double[maxSize];
    for (int i = 0; i < maxSize; i++) {
      d[i] = ThreadLocalRandom.current().nextDouble(0.00001, 1);
    }

    return d;
  }
}
