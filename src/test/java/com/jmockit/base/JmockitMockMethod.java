package com.jmockit.base;

import mockit.Expectations;
import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;

/**
 * mock类/对象的某一个方法
 *
 * <p>当在Expectations构造函数中传入类时，并录制了方法后，jmockit录制的脚本将对此类的所有实例的录制方法生效
 *
 * <p>当在Expectations构造函数中传入类的实例，并录制了方法后，jmockit录制的脚本将对此实例的录制方法生效，其他实例不受影响
 *
 * @author liujun
 * @version 0.0.1
 * @date 2019/10/13
 */
public class JmockitMockMethod {

  /** 仅mock类的一个方法,以java的Calendar为例 */
  @Test
  public void mockClassMethod() {

    Calendar calendar = Calendar.getInstance();

    new Expectations(Calendar.class) {
      {
        // 当调用获取小时的参数为小时，则返回7点钟
        calendar.get(Calendar.HOUR_OF_DAY);
        result = 7;
      }
    };

    Calendar calendarNew = Calendar.getInstance();

    // 当对象被mock后，所有的实例对象均受影响，当获取小时时都返回为7点
    Assert.assertTrue(calendar.get(Calendar.HOUR_OF_DAY) == 7);
    Assert.assertTrue(calendarNew.get(Calendar.HOUR_OF_DAY) == 7);
    // 其他参数不受影响
    Assert.assertNotEquals(7, calendarNew.get(Calendar.HOUR));
  }

  /** 仅mock类的一个方法,以java的Calendar为例,一个方法的任意参数都受影响 */
  @Test
  public void mockClassMethodParamall() {

    Calendar calendar = Calendar.getInstance();

    new Expectations(Calendar.class) {
      {
        // 当调用获取小时的参数为小时，则返回7点钟
        calendar.get(anyInt);
        result = 7;
      }
    };

    Calendar calendarNew = Calendar.getInstance();

    // 当对象被mock后，所有的实例对象均受影响，当获取任意参数都返回为7
    Assert.assertTrue(calendar.get(Calendar.HOUR_OF_DAY) == 7);
    Assert.assertTrue(calendarNew.get(Calendar.HOUR_OF_DAY) == 7);
    Assert.assertTrue(calendarNew.get(Calendar.HOUR) == 7);
    Assert.assertTrue(calendarNew.get(Calendar.MONDAY) == 7);
  }

  /** 影响类的单个实例对象 */
  @Test
  public void mockInstanceMethod() {

    Calendar calendar = Calendar.getInstance();

    new Expectations(calendar) {
      {
        // 当此实例获取任意参数时，都返回7小时
        calendar.get(anyInt);
        result = 7;
      }
    };

    Calendar calendarNew = Calendar.getInstance();

    Assert.assertNotEquals(7, calendarNew.get(Calendar.YEAR));
    Assert.assertEquals(7, calendar.get(Calendar.YEAR));
    Assert.assertEquals(7, calendar.get(Calendar.HOUR_OF_DAY));
    Assert.assertEquals(7, calendar.get(Calendar.MONDAY));
  }
}
