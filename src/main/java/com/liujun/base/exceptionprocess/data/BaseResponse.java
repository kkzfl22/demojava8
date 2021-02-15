package com.liujun.base.exceptionprocess.data;

import com.liujun.base.exceptionprocess.IResponseEnum;
import com.liujun.base.exceptionprocess.enums.CommonResponseEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 基础返回结果
 *
 * @author sprainkle
 * @date 2019/5/2
 */
@Getter
@Setter
@ToString
public class BaseResponse {
  /** 返回码 */
  protected int code;
  /** 返回消息 */
  protected String message;

  public BaseResponse() {
    // 默认创建成功的回应
    this(CommonResponseEnum.SUCCESS);
  }

  public BaseResponse(IResponseEnum responseEnum) {
    this(responseEnum.getCode(), responseEnum.getMessage());
  }

  public BaseResponse(int code, String message) {
    this.code = code;
    this.message = message;
  }
}
