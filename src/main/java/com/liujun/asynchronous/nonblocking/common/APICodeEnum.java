package com.liujun.asynchronous.nonblocking.common;

/**
 * 公共的操作枚举信息
 *
 * @author liujun
 * @version 0.0.1
 */
public enum APICodeEnum {
  /** 错误码:操作成功 */
  SUCCESS(new ErrorData(0, "操作成功")),

  /** 错误码: 操作失败 */
  FAIL(new ErrorData(-1, "操作失败")),
  ;

  private ErrorData errorData;

  APICodeEnum(ErrorData errorData) {
    this.errorData = errorData;
  }

  public ErrorData getErrorData() {
    return errorData;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("APICodeEnum{");
    sb.append("errorData=").append(errorData);
    sb.append('}');
    return sb.toString();
  }
}
