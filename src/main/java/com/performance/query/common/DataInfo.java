/*
 * Copyright (C), 2008-2021, Paraview All Rights Reserved.
 */
package com.performance.query.common;

/**
 * @author liujun
 * @since 2021/4/12
 */
public class DataInfo {

  private int id;

  private String data;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("DataInfo{");
    sb.append("id=").append(id);
    sb.append(", data='").append(data).append('\'');
    sb.append('}');
    return sb.toString();
  }
}
