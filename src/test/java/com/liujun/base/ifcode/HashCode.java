package com.liujun.base.ifcode;

import org.junit.Test;

/**
 * @author liujun
 * @version 0.0.1
 */
public class HashCode {

  @Test
  public void testHashOut2() {
    for (int i = 0; i < 100; i++) {
      Object key = String.valueOf(i);
      int h = (h = key.hashCode()) ^ (h >>> 16);
      System.out.println("hashCode:" + key.hashCode() + ":-->" + h);
    }
  }
}
