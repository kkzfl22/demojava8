package com.liujun.base.classLink.classLoaderOrder;

/**
 * 进行类加载顺序的试验,在单一的一个类中进行测试
 *
 * @author liujun
 * @version 0.0.1
 * @date 2019/03/03
 */
public class ClassStaticLoaderOrder {

  /** 静态普通方法 */
  private static final int INTVAL = RunFunciotn();

  static {
    System.out.println("当前静态方法块");
  }

  public ClassStaticLoaderOrder() {
    System.out.println("当前构造方法");
  }

  private static final int RunFunciotn() {
    System.out.println("当前执行静态的普通方法");
    return -100;
  }

  /** 普通方法 */
  public void runNormalFunction() {
    System.out.println("当前普通的方法");
  }
}
