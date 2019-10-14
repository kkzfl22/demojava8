package com.jmockit.base;

import mockit.Mocked;
import org.junit.Assert;
import org.junit.Test;

import javax.servlet.http.HttpSession;
import java.util.Locale;

/**
 * @Mocked注解基本用法用途
 *
 * @mocked修饰的类和接口，是告诉jmockit，帮我生成一个mocked对象，这个对象方法返回默认值
 *     <p>当返回String类型时，则为null
 *     <p>当返回为原始数值类型时（short,int,float,double,long)就返回0
 *     <p>当返回为其他引用类型时，则返回这个引用类型的mocked对象
 * @mocked的使用场景： 当我们测试程序依赖某个接口时，用@mocked就非常的合适，只需要@Mocked一个注解，jmockit就能帮我们生成这个对象的实例
 * @author liujun
 * @version 0.0.1
 * @date 2019/10/13
 */
public class JmockitMockedAnnotation {

  /** 加上jmockit的APi @Mocked,jmockit会帮我们实体化这个对象，不用担心对象为null */
  @Mocked private Locale locale;

  /** 使用jmockit的API @mocked,jmocket会生成这个接口的实体化对象，不会为null */
  @Mocked private HttpSession session;

  /** 测试mock注解放置于类的效果 */
  @Test
  public void testMockClass() {
    // 正常返回的是当前的国家和地区，即zh,cn
    // 当mocked放在类时，静态方法不起作用，返回为null
    Assert.assertTrue(Locale.getDefault() == null);
    // 非静态方法也不起作用返回为null
    Assert.assertTrue(locale.getCountry() == null);
    // 自己new一个对象也同样如此,被mock过了，返回为null
    Locale localNew = new Locale("zh", "CN");
    Assert.assertTrue(localNew.getCountry() == null);
  }

  /** 测试mocked注解放置于接口的效果 */
  @Test
  public void testMockInterface() {
    // 返回值为null，不起作为，方法返回为nul
    Assert.assertTrue(session.getId() == null);
    // 返回值为原始类型,值也是不起作用，返回为原始类型的默认值
    Assert.assertTrue(session.getCreationTime() == 0L);
    // 当返回类型为对象时,jmockit会构建对象,返回类型不为null,即mock帮你构建了二层对象
    Assert.assertTrue(session.getServletContext() != null);
    // jmockit构建的二层对象的方法也返回的是默认值，即对象为null，原始类型为默认值
    Assert.assertTrue(session.getServletContext().getContextPath() == null);
  }
}
