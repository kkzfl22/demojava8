package com.liujun.base.exceptionprocess.assertion;

import com.liujun.base.exceptionprocess.exception.BaseException;

/**
 * 用于进行异常的处理的断言
 *
 * @author liujun
 * @version 0.0.1
 */
public interface Assert {

  /**
   * 创建异常
   *
   * @param args
   * @return
   */
  BaseException newException(Object... args);

  /**
   * 创建异常
   *
   * @param t 异常信息
   * @param args 参数
   * @return 异常
   */
  BaseException newException(Throwable t, Object... args);

  /**
   * 断言对象<code>obj</code>非空。如果对象<code>obj</code>为空，则抛出异常
   *
   * @param obj 待判断的对象
   */
  default void assertNotNull(Object obj) {
    if (obj == null) {
      throw newException(obj);
    }
  }

  /**
   * 断言对象非空，如果对象<code>obj</code>为空，则抛出异常
   *
   * <p>异常信息<code>message</code>支持传递参数方式，避免在判断之前进行字符串拼接操作
   *
   * @param obj 待判断对象
   * @param args message占位符对应的参数列表
   */
  default void assertNotNull(Object obj, Object... args) {
    if (obj == null) {
      throw newException(args);
    }
  }
}
