package com.liujun.base.exceptionprocess;

import com.liujun.base.exceptionprocess.assertion.BusinessExceptionAssert;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 返回的异常枚举
 *
 * @author liujun
 * @version 0.0.1
 */
@Getter
@AllArgsConstructor
public enum ResponseEnum implements BusinessExceptionAssert {
  /** Bad licence type */
  BAD_LICENCE_TYPE(7001, "Bad licence type."),
  /** Licence not found */
  LICENCE_NOT_FOUND(7002, "Licence not found.");

  /** 返回码 */
  private int code;
  /** 返回消息 */
  private String message;
}
