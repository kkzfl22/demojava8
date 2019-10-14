package com.jmockit.usage.mockclass;

import com.jmockit.usage.BaseClass;
import mockit.Expectations;
import org.junit.Assert;
import org.junit.Test;

/**
 * 使用Expectations来mock类
 *
 * 此mock方式，将对类的所有实例有mock效果
 *
 * @author liujun
 * @version 0.0.1
 * @date 2019/10/13
 */
public class ClassMockByExpectationsTest {
  @Test
  public void testClassMockByExpectations() {
    BaseClass instance = new BaseClass();

    new Expectations(BaseClass.class) {
      {
        // mock静态方法
        BaseClass.staticMethod();
        result = 10;
        // mock普通方法
        instance.ordinaryMethod();
        result = 20;
        // mockfinal方法
        instance.finalMethod();
        result = 30;
        // instance.navtiveMethod();
        // result = 40;
        // native,private方法不进行mock操作，原样返回
      }
    };

    Assert.assertTrue(BaseClass.staticMethod() == 10);
    Assert.assertTrue(instance.ordinaryMethod() == 20);
    Assert.assertTrue(instance.finalMethod() == 30);
    // Assert.assertTrue(instance.navtiveMethod() == 40);
    Assert.assertTrue(instance.callPrivateMethod() == 5);
  }
}
