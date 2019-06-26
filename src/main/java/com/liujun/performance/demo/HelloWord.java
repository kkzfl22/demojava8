package com.liujun.performance.demo;

import java.util.concurrent.TimeUnit;

/**
 * @author liujun
 * @version 0.0.1
 * @date 2019/06/16
 */
public class HelloWord {

  public static void main(String[] args) {

    try {
      Thread.sleep(15000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    int max = 100000;
    for (int i = 0; i < max; i++) {
      System.out.println(i);
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
