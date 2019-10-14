package com.jmockit.hightlevel;

import mockit.Mock;
import mockit.MockUp;
import org.junit.Assert;
import org.junit.Test;

/**
 * 对类中的一些初始化操作进行mock
 *
 * @author liujun
 * @version 0.0.1
 * @date 2019/10/14
 */
public class MockClassInit {

  /** 进行mock构建类，去掉初始化的部分 */
  public static class MockBackClassInitMockUp extends MockUp<BaseAnOrdinaryClassWithBlock> {
    /** mock构造函数和初始化代码块， */
    @Mock
    public void $init(int initValue) {}

    /** 静态代码块的构建 */
    @Mock
    public void $clinit() {}
  }

  /** 对类的初始化进行mock操作 */
  @Test
  public void testMockClassInit() {

    // 初始化mock类
    new MockBackClassInitMockUp();
    BaseAnOrdinaryClassWithBlock instance = new BaseAnOrdinaryClassWithBlock(22);

    System.out.println(instance.getInitValue());
    // 构建函数与代码块被mock
    Assert.assertTrue(instance.getInitValue() == 0);
    // 静态代码块被mock
    Assert.assertTrue(BaseAnOrdinaryClassWithBlock.getStaticInitValue() == 0);
  }
}
