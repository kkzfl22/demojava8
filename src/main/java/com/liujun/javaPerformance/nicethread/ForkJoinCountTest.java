package com.liujun.javaPerformance.nicethread;

import java.util.Arrays;
import java.util.concurrent.*;

/**
 * 进行fork/join相关的功能性试验
 *
 * @author liujun
 * @version 0.0.1
 * @date 2019/08/13
 */
public class ForkJoinCountTest {

  private double[] d;

  public ForkJoinCountTest(double[] d) {
    this.d = d;
  }

  public class ForkJoinTaskCount extends RecursiveTask<Integer> {

    private int first;

    private int last;

    public ForkJoinTaskCount(int first, int last) {
      this.first = first;
      this.last = last;
    }

    @Override
    protected Integer compute() {
      int subCount;
      if (last - first < 3) {
        subCount = 0;
        for (int i = first; i <= last; i++) {
          if (d[i] < 0.5) {
            subCount++;
          }
        }
      } else {
        int mid = (first + last) >>> 1;

        ForkJoinTaskCount left = new ForkJoinTaskCount(first, mid);
        left.fork();
        ForkJoinTaskCount right = new ForkJoinTaskCount(mid + 1, last);
        right.fork();
        subCount = left.join();
        subCount += right.join();
      }
      return subCount;
    }
  }

  public ForkJoinTaskCount getInstance(int first, int last) {
    return new ForkJoinTaskCount(first, last);
  }
}
