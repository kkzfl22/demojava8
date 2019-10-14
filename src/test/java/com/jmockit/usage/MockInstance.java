package com.jmockit.usage;

import mockit.Expectations;
import org.junit.Assert;
import org.junit.Test;

/**
 * 针类的实例对象传入到Expectations的构建函数中，影响的是类的实例对象
 *
 * <p>与mock类来说，此影响的仅为单个实例对象，影响范围更小
 *
 * @author liujun
 * @version 0.0.1
 * @date 2019/10/14
 */
public class MockInstance {

  /** 测试mock实例对象 */
  @Test
  public void testInstanceMockByExpectations() {

    BaseClass instance = new BaseClass();

    new Expectations(instance) {
      {
        instance.finalMethod();
        result = 300;
        instance.ordinaryMethod();
        result = 200;
      }
    };

    Assert.assertTrue(BaseClass.staticMethod() == 1);
    Assert.assertTrue(instance.ordinaryMethod() == 200);
    Assert.assertTrue(instance.finalMethod() == 300);
    Assert.assertTrue(instance.callPrivateMethod() == 5);
  }
}
