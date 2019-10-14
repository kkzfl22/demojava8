package com.jmockit.base.capturing;

/**
 * 权限类，校验用户没有权限访问某资源
 *
 * @author liujun
 * @version 0.0.1
 * @date 2019/10/13
 */
public interface IPrivilege {

  /**
   * 判断用户有没有权限
   *
   * @param userId
   * @return 有权限，就返回true,否则返回false
   */
  boolean isAllow(long userId);
}
