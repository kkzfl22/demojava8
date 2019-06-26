package com.liujun.garbegecollection.cms;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author liujun
 * @version 0.0.1
 * @date 2019/06/26
 */
public class CmsDemoCode {

  private static final Map<Integer, Integer> MAP = new HashMap<>();

  public static void main(String[] args) {
    for (int i = 0; i < 14; i++) {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      System.out.println("第:" + i + "次停顿时间");
    }
    for (int i = 0; i < Integer.MAX_VALUE; i++) {
      Integer sum = new Integer(i) + ThreadLocalRandom.current().nextInt();
      MAP.put(i, sum);
      if (i % 500000 == 0) {
        // System.out.println("...." + i + ",sum:" + sum + ",mapsize;" + MAP.size());
        try {
          Thread.sleep(2000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
