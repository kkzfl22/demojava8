package com.liujun.base.ifcode;


import org.junit.Test;

/**
 * @author liujun
 * @version 0.0.1
 * @date 2019/08/09
 */
public class IfCodeUrn {

  public boolean runcode() {
    System.out.println("运行1");
    return false;
  }

  public boolean runcode2() {
    System.out.println("运行2");
    return false;
  }

  @Test
  public void runTest() {
    if (runcode() & runcode2()) {
      System.out.println("1111");
    }

    System.out.println("--------");

    if (runcode() && runcode2()) {
      System.out.println("1111");
    }
  }
}
