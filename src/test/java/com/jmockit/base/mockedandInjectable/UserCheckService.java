package com.jmockit.base.mockedandInjectable;

/**
 * 用户身份验证
 *
 * @author liujun
 * @version 0.0.1
 * @date 2019/10/13
 */
public interface UserCheckService {

  /**
   * 校验某个用户是否是合法用户
   *
   * @param userId 用户ID
   * @return 合法的就返回true,否则返回false
   */
  boolean check(long userId);
}
