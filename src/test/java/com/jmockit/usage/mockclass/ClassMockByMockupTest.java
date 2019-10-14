package com.jmockit.usage.mockclass;

import com.jmockit.usage.BaseClass;
import mockit.Mock;
import mockit.MockUp;
import org.junit.Assert;
import org.junit.Test;

/**
 * 使用mockup构建对象
 *
 * @author liujun
 * @version 0.0.1
 * @date 2019/10/13
 */
public class ClassMockByMockupTest {

  public static class AnBaseClassMockup extends MockUp<BaseClass> {
    /**
     * 静态公共方法
     *
     * @return
     */
    @Mock
    public static int staticMethod() {
      return 100;
    }

    /**
     * 普通的方法
     *
     * @return
     */
    @Mock
    public int ordinaryMethod() {
      return 200;
    }

    /**
     * 使用final的方法
     *
     * @return
     */
    @Mock
    public final int finalMethod() {
      return 300;
    }

    // native方法,返回4
    // public native int navtiveMethod();

    /**
     * 私有方法
     *
     * @return
     */
    @Mock
    private int privateMethod() {
      return 500;
    }
  }

  @Test
  public void testClassMockByMockup() {
    // 实例化mock对象
    new AnBaseClassMockup();

    BaseClass instance = new BaseClass();

    Assert.assertTrue(BaseClass.staticMethod() == 100);
    Assert.assertTrue(instance.ordinaryMethod() == 200);
    Assert.assertTrue(instance.finalMethod() == 300);
    Assert.assertTrue(instance.callPrivateMethod() == 500);
  }
}
