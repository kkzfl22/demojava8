package com.liujun.asynchronous.nonblocking.common.user;

/**
 * 用户的传输实体
 *
 * @author liujun
 * @version 0.0.1
 */
public class UserDTO {
  /** 用户的id */
  private String userId;

  /** 用户的名称 */
  private String name;

  /** 地址信息 */
  private String address;

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("UserDTO{");
    sb.append("userId='").append(userId).append('\'');
    sb.append(", name='").append(name).append('\'');
    sb.append(", address='").append(address).append('\'');
    sb.append('}');
    return sb.toString();
  }
}
