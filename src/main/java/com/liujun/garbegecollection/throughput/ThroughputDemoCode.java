package com.liujun.garbegecollection.throughput;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 使用垃圾收集参数:-XX:+PrintGCDetails -Xms64M -Xmn64M -XX:+PrintGCTimeStamps
 *
 * @author liujun
 * @version 0.0.1
 * @date 2019/06/25
 */
public class ThroughputDemoCode {

  private static final Map<Integer, Integer> MAP = new HashMap<>();

  public static void main(String[] args) {
    try {
      Thread.sleep(12000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    for (int i = 0; i < Integer.MAX_VALUE; i++) {
      int start = ThreadLocalRandom.current().nextInt();
      Integer sum = new Integer(i) + ThreadLocalRandom.current().nextInt();
      MAP.put(i, sum);
      if (i % 500000 == 0) {
        // System.out.println("...." + i + ",sum:" + sum + ",mapsize;" + MAP.size());
        try {
          Thread.sleep(5000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
