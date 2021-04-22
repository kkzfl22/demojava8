/*
 * Copyright (C), 2008-2021, Paraview All Rights Reserved.
 */
package com.performance.query.disruptor.multithread;

/**
 * 基本的事件信息
 *
 * @author liujun
 * @since 2021/4/10
 */
public class WorkUserEventInfo {

  /** 传递的数据信息 */
  private String data;

  /** 当前的序号 */
  private int index;

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("EventInfo{");
    sb.append("data='").append(data).append('\'');
    sb.append("index='").append(index).append('\'');
    sb.append('}');
    return sb.toString();
  }
}
