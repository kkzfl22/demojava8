package com.liujun.javaPerformance.nicethread;

import java.util.concurrent.RecursiveTask;

/**
 * 使用递归进行求解最小数
 *
 * @author liujun
 * @version 0.0.1
 * @date 2019/08/13
 */
public class CountRecursion {

  private double[] d;

  public CountRecursion(double[] d) {
    this.d = d;
  }

  public int rescursion(int start, int last) {
    int count = 0;
    if (last - start <= 2) {
      for (int i = start; i < last; i++) {
        if (d[i] < 0.5) {
          count++;
        }
      }
    } else {
      // 再次进行划分
      int mid = (last - start) / 2;

      count = this.rescursion(start, start + mid);
      count += this.rescursion(start + mid, last);
    }
    return count;
  }
}
