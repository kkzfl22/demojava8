package com.liujun.asynchronous.nonblocking.invoke.eventbus.constants;

/**
 * 消息类型的枚举
 *
 * @author liujun
 * @version 0.0.1
 */
public enum DataTypeEnum {

  /** 用户标识 */
  USER("user"),

  /** 商品服务 */
  GOODS("goods");

  private String type;

  DataTypeEnum(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("DataTypeEnum{");
    sb.append("type='").append(type).append('\'');
    sb.append('}');
    return sb.toString();
  }
}
