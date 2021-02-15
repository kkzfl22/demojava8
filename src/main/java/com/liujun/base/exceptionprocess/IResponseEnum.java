package com.liujun.base.exceptionprocess;

/**
 * 定义错误码的枚举接口
 *
 * @author liujun
 * @version 0.0.1
 */
public interface IResponseEnum {

  /**
   * 获取错误码
   *
   * @return
   */
  int getCode();

  /**
   * 获取错误消息
   *
   * @return
   */
  String getMessage();
}
