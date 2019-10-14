package com.jmockit.base;

import mockit.Mock;
import mockit.MockUp;
import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;

/**
 * MockUp & @Mock提供的Mock方式,是最直接的录制方式
 *
 * <p>Mockup & @Mock的方式，可以解决80%的场景，但在以下场景，此方法解决不了：
 *
 * <p>1，一个类存在多个实例时，只对其中的一个实例进行mock
 *
 * <p>2,AOP动态生成的类的mock
 *
 * <p>3,对类的所有方法都需要mock,书写mockup的代码量太大，可以使用@Mocked，仅一行代码就可以搞定。
 *
 * @author liujun
 * @version 0.0.1
 * @date 2019/10/13
 */
public class JmockitMockUpMockMethod {

  /** 进行mockup的测试操作 */
  @Test
  public void testMockup() {
    new MockUp<Calendar>(Calendar.class) {
      @Mock
      public int get(int unit) {
        if (unit == Calendar.YEAR) {
          return 2019;
        } else if (unit == Calendar.HOUR_OF_DAY) {
          return 10;
        } else if (unit == Calendar.DAY_OF_MONTH) {
          return 10;
        }
        return 0;
      }
    };

    Calendar cal = Calendar.getInstance();
    Calendar calnew = Calendar.getInstance();

    Assert.assertTrue(cal.get(Calendar.YEAR) == 2019);
    Assert.assertTrue(calnew.get(Calendar.YEAR) == 2019);
    Assert.assertTrue(cal.get(Calendar.DAY_OF_MONTH) == 10);
    Assert.assertTrue(calnew.get(Calendar.DAY_OF_MONTH) == 10);
    Assert.assertTrue(calnew.get(Calendar.MONTH) == 0);
    Assert.assertTrue(cal.get(Calendar.MONTH) == 0);
  }
}
