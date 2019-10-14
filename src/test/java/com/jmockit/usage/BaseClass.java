package com.jmockit.usage;

/**
 * 进行mock类
 *
 * @author liujun
 * @version 0.0.1
 * @date 2019/10/13
 */
public class BaseClass {

  /**
   * 静态公共方法
   *
   * @return
   */
  public static int staticMethod() {
    return 1;
  }

  /**
   * 普通的方法
   *
   * @return
   */
  public int ordinaryMethod() {
    return 2;
  }

  /**
   * 使用final的方法
   *
   * @return
   */
  public final int finalMethod() {
    return 3;
  }

  // native方法,返回4
  // public native int navtiveMethod();

  /**
   * 私有方法
   *
   * @return
   */
  private int privateMethod() {
    return 5;
  }

  /**
   * 调用私有方法
   *
   * @return
   */
  public int callPrivateMethod() {
    return privateMethod();
  }
}
