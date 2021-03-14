package com.liujun.asynchronous.nonblocking.common;

import java.util.List;

/**
 * 公共的返回结果对象
 *
 * @author liujun
 * @version 0.0.1
 */
public class ApiResponse<T> {

  /** 返回的枚举信息 */
  private Boolean result;

  /** 错误码 */
  private int code;

  /** 错误信息 */
  private String msg;

  /** 返回结果 */
  private T data;

  /** 数据返回的条数 */
  private Long count;

  public ApiResponse(Builder<T> builder) {
    this.result = builder.result;
    this.code = builder.code;
    this.msg = builder.msg;
    this.data = builder.data;
    this.count = builder.count;
  }

  /**
   * 用来进行作为参数的build类
   *
   * @author liujun
   * @date 2014年12月16日
   * @vsersion 0.0.1
   */
  public static class Builder<T> {

    /** 返回的枚举信息 */
    private Boolean result;

    /** 错误码 */
    private int code;

    /** 错误信息 */
    private String msg;

    /** 返回结果 */
    private T data;

    /** 数据返回的条数 */
    private Long count;

    public Builder result(Boolean result) {
      this.result = result;
      return this;
    }

    public Builder code(int code) {
      this.code = code;
      return this;
    }

    public Builder msg(String msg) {
      this.msg = msg;
      return this;
    }

    public Builder count(long count) {
      this.count = count;
      return this;
    }

    public Builder data(T data) {
      this.data = data;
      return this;
    }

    public ApiResponse build() {
      return new ApiResponse(this);
    }
  }

  public static ApiResponse.Builder builder() {
    return new ApiResponse.Builder();
  }

  /**
   * 默认的成功对象
   *
   * @return
   */
  public static <T> ApiResponse<T> ok() {
    return ApiResponse.builder()
        .result(Boolean.TRUE)
        .code(APICodeEnum.SUCCESS.getErrorData().getCode())
        .msg(APICodeEnum.SUCCESS.getErrorData().getMsg())
        .build();
  }

  /**
   * 成功并且携带信息
   *
   * @param data
   * @return
   */
  public static <T> ApiResponse<T> ok(List data) {

    // 设置返回结果大小
    long size = 0;
    if (null != data && !data.isEmpty()) {
      size = data.size();
    }

    return ApiResponse.builder()
        .result(Boolean.TRUE)
        .code(APICodeEnum.SUCCESS.getErrorData().getCode())
        .msg(APICodeEnum.SUCCESS.getErrorData().getMsg())
        .data(data)
        .count(size)
        .build();
  }

  /**
   * 成功并且携带信息
   *
   * @param data
   * @return
   */
  public static <T> ApiResponse<T> ok(T data) {
    return ApiResponse.builder()
        .result(Boolean.TRUE)
        .code(APICodeEnum.SUCCESS.getErrorData().getCode())
        .msg(APICodeEnum.SUCCESS.getErrorData().getMsg())
        .data(data)
        .build();
  }

  /**
   * 成功并且携带信息
   *
   * @param data
   * @return
   */
  public static <T> ApiResponse<T> ok(List data, long count) {

    return ApiResponse.builder()
        .result(Boolean.TRUE)
        .code(APICodeEnum.SUCCESS.getErrorData().getCode())
        .msg(APICodeEnum.SUCCESS.getErrorData().getMsg())
        .data(data)
        .count(count)
        .build();
  }

  /**
   * 成功并且携带信息
   *
   * @param code
   * @param data
   * @return
   */
  public static <T> ApiResponse<T> ok(APICodeEnum code, T data) {
    return ApiResponse.builder()
        .result(Boolean.TRUE)
        .code(code.getErrorData().getCode())
        .msg(code.getErrorData().getMsg())
        .data(data)
        .build();
  }

  /**
   * 失败提示
   *
   * @return
   */
  public static <T> ApiResponse<T> fail() {
    return ApiResponse.builder()
        .result(Boolean.FALSE)
        .code(APICodeEnum.FAIL.getErrorData().getCode())
        .msg(APICodeEnum.FAIL.getErrorData().getMsg())
        .build();
  }

  /**
   * 失败提示
   *
   * @return
   */
  public static <T> ApiResponse<T> fail(ErrorData errorData) {

    if (null == errorData) {
      errorData = APICodeEnum.FAIL.getErrorData();
    }

    return ApiResponse.builder()
        .result(Boolean.FALSE)
        .code(errorData.getCode())
        .msg(errorData.getMsg())
        .build();
  }

  /**
   * 失败提示
   *
   * @return
   */
  public static <T> ApiResponse<T> fail(ErrorData errorData, List<ErrorData> errorMsg) {

    if (null == errorData) {
      errorData = APICodeEnum.FAIL.getErrorData();
    }

    return ApiResponse.builder()
        .result(Boolean.FALSE)
        .code(errorData.getCode())
        .msg(errorData.getMsg())
        .data(errorMsg)
        .build();
  }

  /**
   * 失败提示
   *
   * @return
   */
  public static <T> ApiResponse<T> fail(int code, String msg) {
    return ApiResponse.builder().result(Boolean.FALSE).code(code).msg(msg).build();
  }

  /**
   * 失败携带信息
   *
   * @param data
   * @return
   */
  public static <T> ApiResponse<T> fail(T data) {
    return ApiResponse.builder()
        .result(Boolean.FALSE)
        .code(APICodeEnum.FAIL.getErrorData().getCode())
        .msg(APICodeEnum.FAIL.getErrorData().getMsg())
        .data(data)
        .build();
  }

  public Boolean getResult() {
    return result;
  }

  public int getCode() {
    return code;
  }

  public String getMsg() {
    return msg;
  }

  public T getData() {
    return data;
  }

  public Long getCount() {
    return count;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("ApiResponse{");
    sb.append("result=").append(result);
    sb.append(", code=").append(code);
    sb.append(", msg='").append(msg).append('\'');
    sb.append(", data=").append(data);
    sb.append(", count=").append(count);
    sb.append('}');
    return sb.toString();
  }
}
