package com.liujun.base.exceptionprocess;

/**
 * @author liujun
 * @version 0.0.1
 */
public class ParamCheck {

  public void checkNotNull() {
    ResponseEnum.BAD_LICENCE_TYPE.assertNotNull("message is null");
  }
}
