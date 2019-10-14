package com.jmockit.base;

import mockit.Injectable;
import mockit.Mocked;
import org.junit.Assert;
import org.junit.Test;

import java.util.Locale;

/**
 * @Injectable 也是告诉jmockit生成一个实例对象，
 *
 * <p>但@Injectable仅针对其修饰的实例有效
 *
 * <p>而@Mocked是针对其所有的修饰所有实例。 @Injectable对类的静态方法，构造函数没有影响，它仅影响的是一个实例
 *
 *
 *
 * @author liujun
 * @version 0.0.1
 * @date 2019/10/13
 */
public class JmockitMockedAndInjectableAnnotation {

  @Test
  public void testMocked(@Mocked Locale locale) {
    // 静态方法不起作用了,返回了null
    Assert.assertTrue(Locale.getDefault() == null);
    // 非静态方法（返回类型为String）也不起作用了，返回了null
    Assert.assertTrue(locale.getCountry() == null);
    // 自已new一个，也同样如此，方法都被mock了
    Locale chinaLocale = new Locale("zh", "CN");
    Assert.assertTrue(chinaLocale.getCountry() == null);
  }

  @Test
  public void testInjectable(@Injectable Locale locale) {
    // 静态方法不mock
    Assert.assertTrue(Locale.getDefault() != null);
    // 非静态方法（返回类型为String）也不起作用了，返回了null,但仅仅限于locale这个对象
    Assert.assertTrue(locale.getCountry() == null);
    // 自已new一个，并不受影响
    Locale chinaLocale = new Locale("zh", "CN");
    Assert.assertTrue(chinaLocale.getCountry().equals("CN"));
  }
}
