package com.jmockit.base.mockedandInjectable;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import org.junit.Assert;
import org.junit.Test;

/**
 * 进行测试订单服务类的服务 @Tested表示被测试的对象，
 *
 * <p>如果该对象没有赋值，则Jmockit会去实例化它，
 *
 * <p>若@Tested的构造函数有参数，则jmockit会在测试属性&测试参数中查询@Injectable修饰的Mocked对象，注入@Tested对象的构造函数来实例化
 *
 * <p>不然，则无参构建函数来实例化，除了构造函数的注入，jmockit还会通过属性查找的方式把@Injectable对象注入到@Tested对象中
 *
 * <p>注入的匹配规则：先类型，再名称（构造函数名、类的属性名）若找到多个可以注入的@Injectable对象，则选择最优先定义的@Injectable对象
 *
 * <p>当然测试需尽量避免此情况，因为测试属性和测试参数加@Injectable是人为控制的。
 *
 * @author liujun
 * @version 0.0.1
 * @date 2019/10/13
 */
public class OrderServiceTest {

  /** @Tested注解的对象表示我们要测试的对象，这里是需要测试订单类，jmokit会帮我们实例化这个测试对象 */
  @Tested private OrderService orderservice;

  private long testUserId = 1234321l;

  private long testTimeId = 456765L;

  /** orderservice依赖MailService和UserCheckService */
  @Test
  public void testSubmitOrder(
      @Injectable MailService mailService, @Injectable UserCheckService userCheckService) {
    new Expectations() {
      {
        mailService.sendMail(testUserId, anyString);
        result = true;
        userCheckService.check(testUserId);
        result = true;
      }
    };

    // jmockit帮我们实例化了MailService对象，并通过orderservice的构造函数，注入到orderservice对象中
    // jmockit帮我们实例化了UserCheckService对象，并通过orderservice属性，注入到orderservice对象中
    Assert.assertTrue(orderservice.submitOrder(testUserId, testTimeId));
  }
}
