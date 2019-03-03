package com.liujun.base.classLink.classLoaderOrder;


import org.junit.Test;

/**
 * @author liujun
 * @version 0.0.1
 * @date 2019/03/03
 */
public class TestStaticClassLoaderOrder {

  @Test
  public void testRunNormalFunction(){
    ClassStaticLoaderOrder order = new ClassStaticLoaderOrder();
    order.runNormalFunction();
  }
}
