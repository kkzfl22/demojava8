package com.jmockit.hightlevel;

/**
 * 一个包含初始化代码普通类
 *
 * @author liujun
 * @version 0.0.1
 * @date 2019/10/14
 */
public class BaseAnOrdinaryClassWithBlock {

  /** 需要初始化的变量值 */
  private int initValue;

  /** 需要静态初始化的变量值 */
  private static int staticInitValue;

  /** 初始化代码块 */
  {
    initValue = 100;
  }

  /** 静态代码块 */
  static {
    staticInitValue = 10;
  }

  public BaseAnOrdinaryClassWithBlock(int initValue) {
    this.initValue = initValue;
  }

  public int getInitValue() {
    return initValue;
  }

  public static int getStaticInitValue() {
    return staticInitValue;
  }

  public boolean insert(String key,String value) {
    return true;
  }

  public boolean update(String key,String value) {
    return true;
  }

  public boolean delete(String key) {
    return true;
  }

  public String query(String key) {
    return "rs";
  }
}
