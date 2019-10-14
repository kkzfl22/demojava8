package com.jmockit.base;

import mockit.Expectations;
import mockit.Verifications;
import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;

/**
 * Verifications用于做验证
 *
 * @author liujun
 * @version 0.0.1
 * @date 2019/10/13
 */
public class JmockitVerifications {

  @Test
  public void testVerifications() {
    // 录制阶段
    Calendar calendar = Calendar.getInstance();

    new Expectations(Calendar.class) {
      {
        // 对参数cal.get进行录制，并匹配参数
        calendar.get(Calendar.YEAR);
        result = 2018;
        calendar.get(Calendar.HOUR_OF_DAY);
        result = 18;
      }
    };

    // 进行验证
    Calendar calendarNew = Calendar.getInstance();

    Assert.assertTrue(calendarNew.get(Calendar.YEAR) == 2018);
    Assert.assertTrue(calendarNew.get(Calendar.HOUR_OF_DAY) == 18);

    new Verifications() {
      {
        Calendar.getInstance();
        // 用于限定方法调用的次数，也可以不限制
        times = 1;
        // 限制get调用2次
        calendar.get(anyInt);
        times = 2;
      }
    };
  }
}
