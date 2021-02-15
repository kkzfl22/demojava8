package com.jmockit.hightlevel;

import mockit.Mock;
import mockit.MockUp;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

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

    private static final Map<String, String> DATA_MAP = new HashMap<>();

    /** mock构造函数和初始化代码块， */
    @Mock
    public void $init(int initValue) {}

    /** 静态代码块的构建 */
    @Mock
    public void $clinit() {}

    @Mock
    public boolean insert(String key, String value) {
      DATA_MAP.put(key, value);
      return true;
    }

    @Mock
    public boolean update(String key, String value) {
      DATA_MAP.put(key, value);
      return true;
    }

    @Mock
    public boolean delete(String key) {
      DATA_MAP.remove(key);
      return false;
    }

    @Mock
    public String query(String key) {
      return DATA_MAP.get(key);
    }
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

    instance.insert("10", "11111");
    instance.update("10", "22222");
    Assert.assertEquals("22222", instance.query("10"));
  }
}
