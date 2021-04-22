/*
 * Copyright (C), 2008-2021, Paraview All Rights Reserved.
 */
package com.demo.disruptor.base1;

/**
 * 基本的事件信息
 *
 * @author liujun
 * @since 2021/4/10
 */
public class UserEventInfo {

  /** 传递的数据信息 */
  private String data;

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("EventInfo{");
    sb.append("data='").append(data).append('\'');
    sb.append('}');
    return sb.toString();
  }
}
